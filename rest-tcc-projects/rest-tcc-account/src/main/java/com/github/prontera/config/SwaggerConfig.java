package com.github.prontera.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@Configuration
@EnableSwagger2WebFlux
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    private static final String DESCRIPTION;

    static {
        try {
            final ClassPathResource resource = new ClassPathResource("SWAGGER.md");
            if (resource.exists()) {
                final List<String> lines = Files.readLines(new File(resource.getPath()), Charsets.UTF_8);
                DESCRIPTION = String.join("\n", lines);
            } else {
                DESCRIPTION = null;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public SwaggerApiInfo generateApiInfo() {
        return SwaggerApiInfo.builder().title("account-plane").version("2.0.0").serviceUrl(null).build();
    }

    @Bean
    public Docket configure(TypeResolver typeResolver) {
        final SwaggerApiInfo info = generateApiInfo();
        return new Docket(DocumentationType.SWAGGER_2)
            .genericModelSubstitutes(Mono.class, Flux.class, Publisher.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.github.prontera.http"))
            .build()
            .pathMapping("/")
            .useDefaultResponseMessages(false)
            .apiInfo(new ApiInfo(info.getTitle(), DESCRIPTION, info.getVersion(), info.getServiceUrl(), new Contact(null, null, null), null, null, ImmutableList.of()))
            .alternateTypeRules(
                AlternateTypeRules.newRule(
                    typeResolver.resolve(Collection.class, WildcardType.class),
                    typeResolver.resolve(List.class, WildcardType.class))
            )
            //.enableUrlTemplating(true)
            .forCodeGeneration(false);
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .defaultModelsExpandDepth(0)
            .defaultModelRendering(ModelRendering.MODEL)
            .docExpansion(DocExpansion.LIST)
            .displayOperationId(false)
            .build();
    }

    @Getter
    @Builder
    @ToString(callSuper = true)
    @EqualsAndHashCode
    private static class SwaggerApiInfo {

        private final String title;

        private final String version;

        private final String serviceUrl;

    }

}
