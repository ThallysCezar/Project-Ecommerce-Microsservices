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
        
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        corsConfig.setAllowCredentials(true);
        
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Email",
                "X-User-Id",
                "X-User-Role",
                "X-Error-Message"
        ));
        
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}