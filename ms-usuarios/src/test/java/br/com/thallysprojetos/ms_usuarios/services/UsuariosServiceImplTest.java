package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.common_dtos.usuario.Role;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import br.com.thallysprojetos.ms_usuarios.services.impl.UsuariosServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuariosService Unit Tests")
public class UsuariosServiceImplTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuariosServiceImpl usuariosService;

    private UsuariosDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuariosDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setUserName("Test User");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setPassword("password123");
        usuarioDTO.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should find all users successfully")
    void testFindAll_Success() {
        List<UsuariosDTO> expectedUsers = Arrays.asList(usuarioDTO, new UsuariosDTO());
        when(databaseClient.findAll()).thenReturn(expectedUsers);

        List<UsuariosDTO> result = usuariosService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(databaseClient, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void testFindById_Success() {
        when(databaseClient.findById(1L)).thenReturn(Optional.of(usuarioDTO));
        when(modelMapper.map(usuarioDTO, UsuariosDTO.class)).thenReturn(usuarioDTO);

        UsuariosDTO result = usuariosService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(databaseClient, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when user not found by ID")
    void testFindById_UserNotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.findById(999L))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(databaseClient, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void testFindByEmail_Success() {
        when(databaseClient.findByEmail("test@example.com")).thenReturn(Optional.of(usuarioDTO));
        when(modelMapper.map(usuarioDTO, UsuariosDTO.class)).thenReturn(usuarioDTO);

        UsuariosDTO result = usuariosService.findByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(databaseClient, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when user not found by email")
    void testFindByEmail_UserNotFound() {
        when(databaseClient.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.findByEmail("notfound@example.com"))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com o e-mail fornecido");

        verify(databaseClient, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByEmail(anyString())).thenThrow(notFoundException);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(UsuariosDTO.class));

        UsuariosDTO result = usuariosService.createUser(usuarioDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("usuarios.exchange"),
                eq("usuarios.create"),
                eq(usuarioDTO)
        );
    }

    @Test
    @DisplayName("Should throw UsuarioAlreadyExistException when email already exists")
    void testCreateUser_EmailAlreadyExists() {
        UsuariosDTO existingUser = new UsuariosDTO();
        existingUser.setId(2L);
        existingUser.setEmail("test@example.com");

        when(databaseClient.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> usuariosService.createUser(usuarioDTO))
                .isInstanceOf(UsuarioAlreadyExistException.class)
                .hasMessageContaining("já está sendo usado por outro usuário");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUsuarios_Success() {
        UsuariosDTO updatedDTO = new UsuariosDTO();
        updatedDTO.setUserName("Updated Name");
        updatedDTO.setEmail("test@example.com");

        when(databaseClient.findById(1L)).thenReturn(Optional.of(usuarioDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(UsuariosDTO.class));

        UsuariosDTO result = usuariosService.updateUsuarios(1L, updatedDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("Updated Name");
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("usuarios.exchange"),
                eq("usuarios.update"),
                any(UsuariosDTO.class)
        );
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when updating non-existent user")
    void testUpdateUsuarios_UserNotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.updateUsuarios(999L, usuarioDTO))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should throw UsuarioAlreadyExistException when updating to existing email")
    void testUpdateUsuarios_EmailAlreadyExists() {
        UsuariosDTO updatedDTO = new UsuariosDTO();
        updatedDTO.setUserName("Updated Name");
        updatedDTO.setEmail("other@example.com");

        UsuariosDTO existingWithEmail = new UsuariosDTO();
        existingWithEmail.setId(2L);
        existingWithEmail.setEmail("other@example.com");

        when(databaseClient.findById(1L)).thenReturn(Optional.of(usuarioDTO));
        when(databaseClient.findByEmail("other@example.com")).thenReturn(Optional.of(existingWithEmail));

        assertThatThrownBy(() -> usuariosService.updateUsuarios(1L, updatedDTO))
                .isInstanceOf(UsuarioAlreadyExistException.class);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUsuarios_Success() {
        when(databaseClient.existsById(1L)).thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyLong());

        usuariosService.deleteUsuarios(1L);

        verify(databaseClient, times(1)).existsById(1L);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("usuarios.exchange"),
                eq("usuarios.delete"),
                eq(1L)
        );
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when deleting non-existent user")
    void testDeleteUsuarios_UserNotFound() {
        when(databaseClient.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> usuariosService.deleteUsuarios(999L))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

}