package br.com.thallysprojetos.ms_pagamentos.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
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
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI().info(
                new Info()
                        .title("Projeto Boilerplate para Aplicações Java")
                        .version("1.0")
                        .description("O aplicativo de Game Quiz é uma plataforma divertida e interativa onde os usuários podem participar de quizzes" +
                                " de perguntas e respostas. Oferece funcionalidades de login com diferentes roles para os usuários, utiliza JWT com " +
                                "Token Bearer para autenticação segura e apresenta uma variedade de questões com alternativas. " +
                                "Os jogadores podem competir entre si, acompanhar suas pontuações e desfrutar de uma experiência de jogo dinâmica. "
                                + "O código-fonte do projeto esta disponivel no GitHub: "
                                + "https://github.com/ThallysCezar/game-quiz-springboot")
        );
    }

}