package ca.dal.cs.wanderer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@EnableSwagger2
@Configuration
@Profile("!prod")
public class SwaggerConf {


    private static final String AUTHORIZATION_HEADER = "Authorization";

    //method to generate the REST Api info in swagger profile
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Wanderer")
                .description("Map based web-app developed by Group 21")
                .termsOfServiceUrl("open")
                .contact(new Contact("Group 21", "wanderer.com", "mg770477@dal.ca"))
                .version("Version 1")
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("ca.dal.cs.wanderer"))
                .paths(PathSelectors.any())
                .build();
    }

    //below method generates a Authorize button in swagger
    private ApiKey apiKey() {
        return new ApiKey("Token", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    //This method accepts the token and passes it into the header using the authorization header
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Token", authorizationScopes));
    }

}
