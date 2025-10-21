package br.com.thallysprojetos.ms_usuarios.security;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final DatabaseClient databaseClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Tentando carregar usuário com email: {}", email);
        
        try {
            Optional<UsuariosDTO> usuarioOpt = databaseClient.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                log.error("Usuário não encontrado com email: {}", email);
                throw new UsernameNotFoundException("Usuário não encontrado com email: " + email);
            }
            
            UsuariosDTO usuario = usuarioOpt.get();
            log.info("Usuário encontrado: {} com role: {}", usuario.getEmail(), usuario.getRole());
            
            return new CustomUserDetails(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getPassword(),
                    usuario.getRole()
            );
            
        } catch (FeignException.NotFound e) {
            log.error("Usuário não encontrado com email: {}", email);
            throw new UsernameNotFoundException("Usuário não encontrado com email: " + email);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário: {}", e.getMessage());
            throw new UsernameNotFoundException("Erro ao buscar usuário", e);
        }
    }
}
