package com.seoh.khudatastudiodatasetapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI(){
    Info info = new Info()
        .title("KHU Data Studio Dataset API")
        .version("0.0.1")
        .description("KHU Data Studio Dataset API Specification");
    return new OpenAPI()
        .components(new Components())
        .info(info);
  }


}
