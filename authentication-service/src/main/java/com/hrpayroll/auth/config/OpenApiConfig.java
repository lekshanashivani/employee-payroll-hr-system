package com.hrpayroll.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authenticationServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8081");
        server.setDescription("Authentication Service URL");

        Contact contact = new Contact();
        contact.setEmail("support@hrpayroll.com");
        contact.setName("HR Payroll Support");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Authentication Service API")
                .version("1.0.0")
                .contact(contact)
                .description("API documentation for Authentication Service - User authentication and JWT generation")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}

