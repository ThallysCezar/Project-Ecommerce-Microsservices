package br.com.thallysprojetos.ms_usuarios.security;

import br.com.thallysprojetos.common_dtos.usuario.Role;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ownershipValidator")
@AllArgsConstructor
public class OwnershipValidator {

    /**
     * Verifica se o usuário autenticado é o dono do recurso ou é um admin
     * 
     * @param resourceOwnerId ID do dono do recurso
     * @return true se o usuário é dono ou admin, false caso contrário
     */
    public boolean isOwnerOrAdmin(Long resourceOwnerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Usuário não autenticado tentando acessar recurso");
            return false;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getId();
            Role role = userDetails.getRole();
            
            boolean isAdmin = role == Role.ADMIN;
            boolean isOwner = userId.equals(resourceOwnerId);
            
            log.debug("Verificação de ownership: userId={}, resourceOwnerId={}, role={}, isOwner={}, isAdmin={}", 
                    userId, resourceOwnerId, role, isOwner, isAdmin);
            
            return isOwner || isAdmin;
        }
        
        log.warn("Principal não é uma instância de CustomUserDetails");
        return false;
    }

    /**
     * Verifica se o usuário autenticado é admin
     * 
     * @return true se o usuário é admin, false caso contrário
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getRole() == Role.ADMIN;
        }
        
        return false;
    }

    /**
     * Obtém o ID do usuário autenticado
     * 
     * @return ID do usuário ou null se não autenticado
     */
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        
        return null;
    }

    /**
     * Obtém o role do usuário autenticado
     * 
     * @return Role do usuário ou null se não autenticado
     */
    public Role getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getRole();
        }
        
        return null;
    }
}
