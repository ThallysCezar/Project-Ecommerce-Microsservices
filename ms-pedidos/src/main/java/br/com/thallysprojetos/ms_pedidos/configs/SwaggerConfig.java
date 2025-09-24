package br.com.thallysprojetos.ms_pedidos.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
//        final String securitySchemeName = "bearerAuth";
        Contact contato = new Contact()
                .name("Thallys Cézar")
                .url("https://github.com/thallysCezar");

        return new OpenAPI().info(
                new Info()
                        .title("Projeto Ecommerce Monolítico")
                        .version("1.0")
                        .description("This project is a complete e-commerce solution developed with a monolithic architecture." +
                                " The platform is designed to integrate all essential functionalities, such as product catalog, " +
                                " shopping cart, user management, and order processing, into a single and cohesive codebase.")
                        .contact(contato)
        );
    }

}