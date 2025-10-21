package br.com.thallysprojetos.ms_pedidos.security;

import br.com.thallysprojetos.common_dtos.usuario.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por validar se o usuário autenticado tem permissão para acessar um recurso.
 * Implementa regras de ownership (propriedade) e autorização baseada em roles.
 * 
 * Usado em expressões @PreAuthorize nos controllers, como:
 * @PreAuthorize("@ownershipValidator.isOwnerOrAdmin(#userId)")
 */
@Slf4j
@Component
public class OwnershipValidator {

    /**
     * Verifica se o usuário autenticado é o dono do recurso ou é um ADMIN.
     * ADMINs têm acesso total a todos os recursos.
     * 
     * @param resourceOwnerId ID do usuário dono do recurso (ex: userId do pedido)
     * @return true se o usuário autenticado é o dono OU é ADMIN
     */
    public boolean isOwnerOrAdmin(Long resourceOwnerId) {
        if (resourceOwnerId == null) {
            log.warn("resourceOwnerId é null, negando acesso");
            return false;
        }

        Long authenticatedUserId = getAuthenticatedUserId();
        Role authenticatedUserRole = getAuthenticatedUserRole();

        log.debug("Validando ownership - Resource Owner: {}, Authenticated User: {}, Role: {}", 
                resourceOwnerId, authenticatedUserId, authenticatedUserRole);

        // ADMINs têm acesso a tudo
        if (authenticatedUserRole == Role.ADMIN) {
            log.debug("Acesso concedido: usuário é ADMIN");
            return true;
        }

        // Verifica se o usuário é o dono do recurso
        boolean isOwner = resourceOwnerId.equals(authenticatedUserId);
        
        if (isOwner) {
            log.debug("Acesso concedido: usuário é o dono do recurso");
        } else {
            log.warn("Acesso negado: usuário {} não é o dono do recurso {}", 
                    authenticatedUserId, resourceOwnerId);
        }

        return isOwner;
    }

    /**
     * Verifica se o usuário autenticado tem role ADMIN.
     * 
     * @return true se o usuário é ADMIN
     */
    public boolean isAdmin() {
        Role role = getAuthenticatedUserRole();
        boolean admin = role == Role.ADMIN;
        
        log.debug("Verificando se é admin - Role: {}, É admin: {}", role, admin);
        
        return admin;
    }

    /**
     * Extrai o ID do usuário autenticado do contexto de segurança.
     * 
     * @return ID do usuário autenticado
     * @throws IllegalStateException se não houver usuário autenticado ou o principal não for CustomUserDetails
     */
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Nenhum usuário autenticado encontrado no contexto de segurança");
            throw new IllegalStateException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof CustomUserDetails)) {
            log.error("Principal não é uma instância de CustomUserDetails: {}", 
                    principal.getClass().getName());
            throw new IllegalStateException("Principal inválido");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userDetails.getId();
    }

    /**
     * Extrai a role do usuário autenticado do contexto de segurança.
     * 
     * @return Role do usuário autenticado
     * @throws IllegalStateException se não houver usuário autenticado ou o principal não for CustomUserDetails
     */
    public Role getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Nenhum usuário autenticado encontrado no contexto de segurança");
            throw new IllegalStateException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof CustomUserDetails)) {
            log.error("Principal não é uma instância de CustomUserDetails: {}", 
                    principal.getClass().getName());
            throw new IllegalStateException("Principal inválido");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userDetails.getRole();
    }
}
