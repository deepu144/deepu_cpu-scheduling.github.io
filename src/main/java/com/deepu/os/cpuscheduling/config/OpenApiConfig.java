package com.deepu.os.cpuscheduling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CPU Scheduling")
                        .version("1.0.0")
                        .description("Calculate the CPU Scheduling process by ~deepu")
                );
    }
}
