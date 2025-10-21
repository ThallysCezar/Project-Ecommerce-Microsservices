package br.com.thallysprojetos.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Permite todas as origens (em produção, especifique as origens permitidas)
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Métodos HTTP permitidos
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Headers permitidos
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite credenciais (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);
        
        // Headers expostos (que o frontend pode acessar)
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Email",
                "X-User-Id",
                "X-User-Role",
                "X-Error-Message"
        ));
        
        // Tempo máximo de cache da configuração CORS
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}