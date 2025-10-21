package br.com.thallysprojetos.ms_pedidos.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor do Feign Client para propagar o token JWT
 * em chamadas entre microserviços.
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Tenta pegar o token do contexto da requisição HTTP
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Propaga o token para a chamada Feign
                template.header("Authorization", authHeader);
                System.out.println("🔐 [Feign] Propagando token JWT para: " + template.url());
            }
        }
        
        // Se não houver token no request, tenta pegar do SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            if (token != null && !token.isEmpty()) {
                template.header("Authorization", "Bearer " + token);
                System.out.println("🔐 [Feign] Propagando token do SecurityContext para: " + template.url());
            }
        }
    }
}
