package br.com.thallysprojetos.ms_usuarios.services.impl;

import br.com.thallysprojetos.common_dtos.auth.LoginRequestDTO;
import br.com.thallysprojetos.common_dtos.auth.LoginResponseDTO;
import br.com.thallysprojetos.common_dtos.auth.RegisterRequestDTO;
import br.com.thallysprojetos.common_dtos.usuario.Role;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.security.JwtUtil;
import br.com.thallysprojetos.ms_usuarios.services.AuthenticationService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Tentando autenticar usuário: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UsuariosDTO usuario = databaseClient.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getId(),
                    usuario.getRole()
            );

            log.info("Usuário autenticado com sucesso: {} com role: {}", usuario.getEmail(), usuario.getRole());

            return new LoginResponseDTO(
                    token,
                    usuario.getId(),
                    usuario.getUserName(),
                    usuario.getEmail(),
                    usuario.getRole()
            );

        } catch (AuthenticationException e) {
            log.error("Falha na autenticação para o usuário: {}", request.getEmail());
            throw new BadCredentialsException("Email ou senha inválidos");
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }
    }

    @Override
    public UsuariosDTO register(RegisterRequestDTO request) {
        log.info("Tentando registrar novo usuário: {}", request.getEmail());

        try {
            databaseClient.findByEmail(request.getEmail()).ifPresent(user -> {
                throw new UsuarioAlreadyExistException("Email já cadastrado: " + request.getEmail());
            });
        } catch (FeignException.NotFound e) {
            log.debug("Email disponível para registro: {}", request.getEmail());
        }

        UsuariosDTO dto = new UsuariosDTO();
        dto.setUserName(request.getUserName());
        dto.setEmail(request.getEmail());
        dto.setPassword(passwordEncoder.encode(request.getPassword()));
        dto.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        log.info("Enviando mensagem de criação de usuário para RabbitMQ: {}", dto.getEmail());

        try {
            rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.create", dto);
            log.info("Mensagem enviada com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
            throw new RuntimeException("Erro ao registrar usuário", e);
        }

        return dto;
    }

}