package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.dtos.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuariosService {

    private final DatabaseClient databaseClient;
    private final ModelMapper modelMapper;

    public Page<UsuariosDTO> findAll(Pageable page) {
        return databaseClient.findAll(page).map(p -> modelMapper.map(p, UsuariosDTO.class));
    }

    public UsuariosDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, UsuariosDTO.class))
                .orElseThrow(UsuarioNotFoundException::new);
    }

    public UsuariosDTO findByEmail(String email) {
        return databaseClient.findByEmail(email)
                .map(u -> modelMapper.map(u, UsuariosDTO.class))
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o e-mail fornecido."));
    }

    public UsuariosDTO createUser(UsuariosDTO dto) {
        return databaseClient.createUsuarios(dto);
    }

    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
        UsuariosDTO existingUser = databaseClient.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));

        existingUser.setUserName(dto.getUserName());
        existingUser.setEmail(dto.getEmail());
        UsuariosDTO updatedUser = databaseClient.createUsuarios(existingUser);

        return modelMapper.map(updatedUser, UsuariosDTO.class);
    }

    public void deleteUsuarios(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new UsuarioNotFoundException(String.format("Usuarios não encontrado com o id '%s'.", id));
        }
        databaseClient.deleteUsuarios(id);
    }

}