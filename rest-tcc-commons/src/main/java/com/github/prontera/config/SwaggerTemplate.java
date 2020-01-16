package com.github.prontera.config;

import com.fasterxml.classmate.TypeResolver;
import com.github.pagehelper.PageInfo;
import com.github.prontera.model.swagger.SwaggerApiInfo;
import com.github.prontera.model.swagger.SwaggerPaginationResponse;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerTemplate {
    private static final String DESCRIPTION;

    static {
        try {
            final ClassPathResource resource = new ClassPathResource("SWAGGER.md");
            if (resource.exists()) {
                final InputStream inputStream = resource.getInputStream();
                final List<String> lines = IOUtils.readLines(inputStream, Charsets.UTF_8);
                DESCRIPTION = lines.stream().collect(Collectors.joining("\n"));
            } else {
                DESCRIPTION = null;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SwaggerApiInfo apiInfo() {
        return SwaggerApiInfo.builder().title("Solar").version("v1").serviceUrl(null).statusList(ImmutableList.of()).build();
    }

    @Bean
    public Docket configure(SwaggerApiInfo info, TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.prontera"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.OPTIONS, info.getStatusList())
                .apiInfo(new ApiInfo(info.getTitle(), DESCRIPTION, info.getVersion(), info.getServiceUrl(), new Contact(null, null, null), null, null))
                .alternateTypeRules(
                        AlternateTypeRules.newRule(
                                typeResolver.resolve(PageInfo.class, WildcardType.class),
                                typeResolver.resolve(SwaggerPaginationResponse.class, WildcardType.class)),
                        AlternateTypeRules.newRule(
                                typeResolver.resolve(Collection.class, WildcardType.class),
                                typeResolver.resolve(List.class, WildcardType.class))
                )
                //.enableUrlTemplating(true)
                .forCodeGeneration(false);
    }

    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(
                "validatorUrl",           // url
                "list",       // docExpansion          => none | list
                "alpha",      // apiSorter             => alpha
                "schema",     // defaultModelRendering => schema
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
                false,        // enableJsonEditor      => true | false
                true,
                null);
    }

}