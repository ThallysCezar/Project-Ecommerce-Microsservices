package br.com.thallysprojetos.api_gateway.config;

import br.com.thallysprojetos.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-usuarios", r -> r
                        .path("/ms-usuarios/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://ms-usuarios")
                )
                
                .route("ms-produtos", r -> r
                        .path("/ms-produtos/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://ms-produtos")
                )
                
                .route("ms-pedidos", r -> r
                        .path("/ms-pedidos/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://ms-pedidos")
                )
                
                .route("ms-pagamentos", r -> r
                        .path("/ms-pagamentos/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://ms-pagamentos")
                )
                
                .route("ms-database", r -> r
                        .path("/ms-database/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://ms-database")
                )
                
                .build();
    }

}