package br.com.thallysprojetos.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Filtro de autenticação JWT para Spring Cloud Gateway.
 * Valida tokens JWT nas requisições e propaga informações do usuário para downstream services.
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret:minha-chave-secreta-super-segura-para-jwt-com-minimo-256-bits-de-seguranca}")
    private String secret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            String method = request.getMethod().toString();
            
            System.out.println("🔍 [JWT Filter] Path: " + path + " | Method: " + method);

            // Endpoints públicos (não requerem autenticação)
            if (isPublicEndpoint(request)) {
                System.out.println("✅ [JWT Filter] Endpoint público detectado - liberando sem token");
                return chain.filter(exchange);
            }

            System.out.println("🔒 [JWT Filter] Endpoint protegido - verificando token");

            // Extrai o token do header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("❌ [JWT Filter] Token JWT não fornecido");
                return onError(exchange, "Token JWT não fornecido", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Token JWT inválido ou mal formatado", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Valida o token
                if (!validateToken(token)) {
                    return onError(exchange, "Token JWT expirado ou inválido", HttpStatus.UNAUTHORIZED);
                }

                // Extrai informações do token
                Claims claims = extractAllClaims(token);
                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);

                // Adiciona headers customizados com informações do usuário para os microserviços
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Email", email)
                        .header("X-User-Id", String.valueOf(userId))
                        .header("X-User-Role", role)
                        .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                return chain.filter(modifiedExchange);

            } catch (Exception e) {
                return onError(exchange, "Erro ao processar token JWT: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Verifica se o endpoint é público (não requer autenticação).
     */
    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        System.out.println("📋 [isPublicEndpoint] Verificando: " + method + " " + path);
        
        // Endpoints sempre públicos
        if (path.contains("/auth/") ||
            path.contains("/swagger-ui") ||
            path.contains("/v3/api-docs") ||
            path.contains("/actuator")) {
            System.out.println("✅ [isPublicEndpoint] Matched: endpoint de infraestrutura");
            return true;
        }
        
        // Endpoints de usuários públicos (apenas POST)
        if (method.equals("POST")) {
            System.out.println("🔍 [isPublicEndpoint] Método POST detectado, verificando paths...");
            System.out.println("   Path atual: '" + path + "'");
            System.out.println("   Comparando com: '/auth/register'");
            System.out.println("   Comparando com: '/auth/login'");
            
            // IMPORTANTE: O StripPrefix já removeu /ms-usuarios antes de chegar aqui!
            // Apenas /auth/register e /auth/login são públicos
            // POST /usuarios requer autenticação ADMIN
            if (path.equals("/auth/register") ||
                path.equals("/auth/login")) {
                System.out.println("✅ [isPublicEndpoint] Matched: endpoint de autenticação público!");
                return true;
            }
        }
        
        // Produtos públicos (listagem GET)
        // IMPORTANTE: O StripPrefix já removeu /ms-produtos antes de chegar aqui!
        if (method.equals("GET") && path.contains("/produtos")) {
            if (!path.contains("/update") && !path.contains("/delete")) {
                System.out.println("✅ [isPublicEndpoint] Matched: listagem de produtos");
                return true;
            }
        }
        
        System.out.println("❌ [isPublicEndpoint] Nenhuma regra matched - endpoint protegido");
        return false;
    }

    /**
     * Valida se o token JWT é válido e não expirou.
     */
    private boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrai todos os claims do token JWT.
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retorna uma resposta de erro.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("X-Error-Message", message);
        return exchange.getResponse().setComplete();
    }

    /**
     * Classe de configuração para o filtro.
     */
    public static class Config {
        // Pode adicionar configurações customizadas aqui se necessário
    }

}