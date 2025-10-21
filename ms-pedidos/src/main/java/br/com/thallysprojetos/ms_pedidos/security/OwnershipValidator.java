package br.com.thallysprojetos.ms_pedidos.security;

import br.com.thallysprojetos.common_dtos.usuario.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OwnershipValidator {

    public boolean isOwnerOrAdmin(Long resourceOwnerId) {
        if (resourceOwnerId == null) {
            log.warn("resourceOwnerId é null, negando acesso");
            return false;
        }

        Long authenticatedUserId = getAuthenticatedUserId();
        Role authenticatedUserRole = getAuthenticatedUserRole();

        log.debug("Validando ownership - Resource Owner: {}, Authenticated User: {}, Role: {}", resourceOwnerId, authenticatedUserId, authenticatedUserRole);

        if (authenticatedUserRole == Role.ADMIN) {
            log.debug("Acesso concedido: usuário é ADMIN");
            return true;
        }

        boolean isOwner = resourceOwnerId.equals(authenticatedUserId);

        if (isOwner) {
            log.debug("Acesso concedido: usuário é o dono do recurso");
        } else {
            log.warn("Acesso negado: usuário {} não é o dono do recurso {}", authenticatedUserId, resourceOwnerId);
        }

        return isOwner;
    }

    public boolean isAdmin() {
        Role role = getAuthenticatedUserRole();
        boolean admin = role == Role.ADMIN;

        log.debug("Verificando se é admin - Role: {}, É admin: {}", role, admin);

        return admin;
    }

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Nenhum usuário autenticado encontrado no contexto de segurança");
            throw new IllegalStateException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            log.error("Principal não é uma instância de CustomUserDetails: {}", principal.getClass().getName());
            throw new IllegalStateException("Principal inválido");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userDetails.getId();
    }

    public Role getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Nenhum usuário autenticado encontrado no contexto de segurança");
            throw new IllegalStateException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            log.error("Principal não é uma instância de CustomUserDetails: {}", principal.getClass().getName());
            throw new IllegalStateException("Principal inválido");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userDetails.getRole();
    }

}