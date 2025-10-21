package br.com.thallysprojetos.ms_usuarios.controllers;

import br.com.thallysprojetos.common_dtos.auth.LoginRequestDTO;
import br.com.thallysprojetos.common_dtos.auth.LoginResponseDTO;
import br.com.thallysprojetos.common_dtos.auth.RegisterRequestDTO;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Endpoint para login de usuários
     * 
     * @param request Credenciais de login (email e senha)
     * @return Token JWT e informações do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Requisição de login recebida para: {}", request.getEmail());
        LoginResponseDTO response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro de novos usuários
     * 
     * @param request Dados do novo usuário
     * @return Dados do usuário criado (processamento assíncrono)
     */
    @PostMapping("/register")
    public ResponseEntity<UsuariosDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Requisição de registro recebida para: {}", request.getEmail());
        UsuariosDTO usuario = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(usuario);
    }
}
