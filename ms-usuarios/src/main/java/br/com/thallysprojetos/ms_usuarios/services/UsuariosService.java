package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;

import java.util.List;

public interface UsuariosService {

    List<UsuariosDTO> findAll();

    UsuariosDTO findById(Long id);

    UsuariosDTO findByEmail(String email);

    UsuariosDTO createUser(UsuariosDTO dto);

    UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto);

    void deleteUsuarios(Long id);

}