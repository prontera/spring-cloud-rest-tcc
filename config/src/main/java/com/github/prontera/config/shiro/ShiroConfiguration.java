package com.github.prontera.config.shiro;

import com.github.prontera.config.datasource.DruidDataSourceConfiguration;
import com.github.prontera.config.mybatis.MybatisConfiguration;
import com.github.prontera.security.session.eis.RedisSessionDAO;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author Solar
 */
@Configuration
@AutoConfigureBefore({DruidDataSourceConfiguration.class, MybatisConfiguration.class})
public class ShiroConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroConfiguration.class);

    @Bean
    public DefaultWebSecurityManager securityManager(SessionManager sessionManager,
                                                     RememberMeManager rememberMeManager,
                                                     Collection<Realm> realms) {
        LOGGER.debug("Setting security manager");
        final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        // TODO cache manager here
        securityManager.setRememberMeManager(rememberMeManager);
        securityManager.setRealms(realms);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroConfiguration.LOGGER.debug("Setting Shiro Filter");
        final ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl("/login");
        final LinkedHashMap<String, String> filterChains = new LinkedHashMap<>();
        filterChains.put("/static/**", Filter.ANON);
        filterChains.put("/public/**", Filter.ANON);
        filterChains.put("/js/**", Filter.ANON);
        filterChains.put("/css/**", Filter.ANON);
        filterChains.put("/druid/**", Filter.USER);
        ShiroConfiguration.LOGGER.debug("Creating filter chain {}", filterChains);
        factoryBean.setFilterChainDefinitionMap(filterChains);
        return factoryBean;
    }

    @Configuration
    protected static class ShiroSessionConfiguration {
        @Autowired(required = false)
        Collection<SessionListener> sessionListeners;
        /**
         * Could not inject {@code RedisTemplate<Serializable, Session>}
         */
        @Autowired
        private RedisTemplate redisTemplate;

        @Bean
        public SessionDAO sessionDAO() {
            LOGGER.debug("Setting session DAO");
            return new RedisSessionDAO(redisTemplate);
        }

        @Bean
        public DefaultWebSessionManager sessionManager(@Qualifier("sessionCookie") SimpleCookie sessionCookie) {
            LOGGER.debug("Creating session manager");
            final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
            // Session expire time in mill unit
            sessionManager.setGlobalSessionTimeout(20 * 1000);
            // Redis Recommend
            sessionManager.setSessionDAO(sessionDAO());
            // Listeners for create, stop or expiration
            if (sessionListeners != null && !sessionListeners.isEmpty()) {
                sessionManager.setSessionListeners(sessionListeners);
            }
            // Turn off session validation cause redis has controlled the expire of session
            sessionManager.setSessionValidationSchedulerEnabled(false);
            // commonly set for change session cookies name (default JSESSIOINID)
            sessionManager.setSessionIdCookie(sessionCookie);
            return sessionManager;
        }
    }

    @Configuration
    protected static class ShiroCookieConfiguration {

        @Bean(name = "sessionCookie")
        public SimpleCookie sessionCookie() {
            LOGGER.debug("Creating session cookie bean");
            final SimpleCookie sessionCookie = new SimpleCookie("sessionCookie");
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(-1);
            return sessionCookie;
        }

        @Bean
        public SimpleCookie rememberCookie() {
            LOGGER.debug("Creating remember cookie bean");
            final SimpleCookie rememberCookie = new SimpleCookie("rememberCookie");
            rememberCookie.setPath("/");
            rememberCookie.setHttpOnly(true);
            // second unit
            rememberCookie.setMaxAge(7 * 24 * 60 * 60);
            return rememberCookie;
        }

        @Bean
        public RememberMeManager rememberMeManager(@Qualifier("rememberCookie") SimpleCookie rememberCookie) {
            LOGGER.debug("Creating remember manager");
            final CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
            rememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
            rememberMeManager.setCookie(rememberCookie);
            return rememberMeManager;
        }
    }

    @Configuration
    protected static class Processor {

        @Bean
        public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
            return new LifecycleBeanPostProcessor();
        }

        @Bean
        public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
            final DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
            proxyCreator.setProxyTargetClass(true);
            return proxyCreator;
        }
    }

    class Filter {
        // roles, perms, port, rest are NOT support
        static final String SSL = "ssl";
        static final String ANON = "anon";
        static final String AUTHC = "authc";
        static final String AUTHC_BASIC = "authcBasic";
        static final String LOGOUT = "logout";
        static final String USER = "user";
        static final String NO_SESSION = "noSessionCreation";
    }

}
