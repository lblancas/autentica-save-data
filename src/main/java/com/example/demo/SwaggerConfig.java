package com.example.demo;

/**
 *
 * @author egarnica
 */
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Segurenta api.
     *
     * @return docket
     */
    @Bean
    public Docket multivaCecobanApi() {

        return new Docket(DocumentationType.SWAGGER_2).select()
            .apis(RequestHandlerSelectors.basePackage("com.example.demo.web"))
            .paths(regex("/.*")).build().apiInfo(metaData())
            .useDefaultResponseMessages(false);

    }

    /**
     * Meta data.
     *
     * @return api info
     */
    private ApiInfo metaData() {

        return new ApiInfoBuilder().title("Servicio Catalogo Segurenta")
            .description("\"Se listan y obtiene los tipos de catalogos\"").version("1.0.0")
            .license("Apache License Version 2.0")
            .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
            .build();
    }

    /**
     * Adds the resource handlers.
     *
     * @param registry
     *            registry
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.config.annotation.
     * WebMvcConfigurationSupport#addResourceHandlers(org.springframework.web.
     * servlet.config.annotation.ResourceHandlerRegistry)
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}