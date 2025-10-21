package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.common_dtos.auth.LoginRequestDTO;
import br.com.thallysprojetos.common_dtos.auth.LoginResponseDTO;
import br.com.thallysprojetos.common_dtos.auth.RegisterRequestDTO;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;

public interface AuthenticationService {
    LoginResponseDTO login(LoginRequestDTO request);
    UsuariosDTO register(RegisterRequestDTO request);
}
