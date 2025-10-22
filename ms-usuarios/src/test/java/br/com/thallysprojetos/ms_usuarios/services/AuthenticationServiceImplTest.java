package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.common_dtos.auth.LoginRequestDTO;
import br.com.thallysprojetos.common_dtos.auth.LoginResponseDTO;
import br.com.thallysprojetos.common_dtos.auth.RegisterRequestDTO;
import br.com.thallysprojetos.common_dtos.usuario.Role;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.security.JwtUtil;
import br.com.thallysprojetos.ms_usuarios.services.impl.AuthenticationServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Unit Tests")
public class AuthenticationServiceImplTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;
    private UsuariosDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setUserName("Test User");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.USER);

        usuarioDTO = new UsuariosDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setUserName("Test User");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setPassword("encodedPassword");
        usuarioDTO.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLogin_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(databaseClient.findByEmail("test@example.com"))
                .thenReturn(Optional.of(usuarioDTO));
        when(jwtUtil.generateToken(anyString(), anyLong(), any(Role.class)))
                .thenReturn("mocked.jwt.token");

        LoginResponseDTO result = authenticationService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("test@example.com", 1L, Role.USER);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void testLogin_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Email ou senha inválidos");

        verify(databaseClient, never()).findByEmail(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong(), any(Role.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found after authentication")
    void testLogin_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Email ou senha inválidos"));

        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Email ou senha inválidos");

        verify(jwtUtil, never()).generateToken(anyString(), anyLong(), any(Role.class));
        verify(databaseClient, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegister_Success() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByEmail("newuser@example.com"))
                .thenThrow(notFoundException);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(UsuariosDTO.class));

        UsuariosDTO result = authenticationService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getRole()).isEqualTo(Role.USER);

        verify(passwordEncoder, times(1)).encode("password123");
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("usuarios.exchange"),
                eq("usuarios.create"),
                any(UsuariosDTO.class)
        );
    }

    @Test
    @DisplayName("Should throw UsuarioAlreadyExistException when email already exists")
    void testRegister_EmailAlreadyExists() {
        UsuariosDTO existingUser = new UsuariosDTO();
        existingUser.setEmail("newuser@example.com");
        when(databaseClient.findByEmail("newuser@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(UsuarioAlreadyExistException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(passwordEncoder, never()).encode(anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should register user with default USER role when role is null")
    void testRegister_DefaultRole() {
        registerRequest.setRole(null);
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByEmail("newuser@example.com"))
                .thenThrow(notFoundException);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(UsuariosDTO.class));

        UsuariosDTO result = authenticationService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.USER);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("usuarios.exchange"),
                eq("usuarios.create"),
                any(UsuariosDTO.class)
        );
    }

    @Test
    @DisplayName("Should throw RuntimeException when RabbitMQ fails during registration")
    void testRegister_RabbitMQFailure() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByEmail("newuser@example.com"))
                .thenThrow(notFoundException);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(UsuariosDTO.class));

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao registrar usuário");

        verify(passwordEncoder, times(1)).encode("password123");
    }

}