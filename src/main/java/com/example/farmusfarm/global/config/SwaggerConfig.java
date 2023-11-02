package com.example.farmusfarm.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    private static final String API_NAME = "Farmus Farm Service API";
    private static final String API_VERSION = "0.0.2";
    private static final String API_DESCRIPTION = "팜어스 팜 서비스 api 명세서입니다.";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false) //기본 응답코드 표시
                .apiInfo(apiInfo()) //Api 정보
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.farmusfarm.domain")) //적용할 패키지명
                .paths(PathSelectors.any()) //패키지 하위에서 적용할 url path 지정
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .build();
    }
}

