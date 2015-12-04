package com.github.prontera.config.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.prontera.config.datasource.DruidDataSourceConfiguration;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter({DruidDataSourceConfiguration.class, MybatisAutoConfiguration.class})
public class MybatisConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Autowired
    private MybatisProperties properties;

    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Bean(name = "sessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               @Value("${mybatis.pagination-enable:false}") boolean isPaginationEnable) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        LOGGER.debug("Mybatis SqlSessionFactoryBean set datasource");
        factory.setDataSource(dataSource);
        if (StringUtils.hasText(properties.getConfig())) {
            LOGGER.debug("Loading mybatis configuration file");
            factory.setConfigLocation(
                    resourceLoader.getResource(properties.getConfig()));
        } else {
            LOGGER.debug("Setting 'application.properties' into Mybatis SqlSessionFactoryBean");
            factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
            factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
            factory.setMapperLocations(properties.getMapperLocations());
            if (isPaginationEnable) {
                enablePagination(factory);
            }
        }
        return factory.getObject();
    }

    private void enablePagination(SqlSessionFactoryBean factory) {
        LOGGER.debug("Enabling pagination component (PageHelper)");
        // pagination setting
        final PageHelper pageHelper = new PageHelper();
        final Properties pageHelperPros = new Properties();
        pageHelperPros.setProperty("pageSizeZero", "true");
        pageHelper.setProperties(pageHelperPros);
        // register pagination component
        factory.setPlugins(new Interceptor[]{pageHelper});
    }


}
