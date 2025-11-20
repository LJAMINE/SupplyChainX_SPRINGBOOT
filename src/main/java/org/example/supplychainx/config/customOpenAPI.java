package org.example.supplychainx.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Swagger/OpenAPI config to show Basic auth in UI

public class customOpenAPI {

    @Bean
    public OpenAPI customOpenAPI() {
        final String basicScheme = "basicAuth";
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(basicScheme,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                ))
                .addSecurityItem(new SecurityRequirement().addList(basicScheme))
                .info(new Info().title("SupplyChainX API").version("v1"));
    }
}
