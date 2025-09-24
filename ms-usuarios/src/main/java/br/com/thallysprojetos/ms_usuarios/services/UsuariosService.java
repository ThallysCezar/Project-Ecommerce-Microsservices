package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.ms_usuarios.dtos.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import br.com.thallysprojetos.ms_usuarios.models.Usuarios;
import br.com.thallysprojetos.ms_usuarios.repositories.UsuariosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final ModelMapper modelMapper;

    public Page<UsuariosDTO> findAll(Pageable page) {
        return usuariosRepository.findAll(page).map(p -> modelMapper.map(p, UsuariosDTO.class));
    }

    public UsuariosDTO findById(Long id) {
        return usuariosRepository.findById(id)
                .map(p -> modelMapper.map(p, UsuariosDTO.class))
                .orElseThrow(UsuarioNotFoundException::new);
    }

    public UsuariosDTO findByEmail(String email) {
        return usuariosRepository.findByEmail(email)
                .map(u -> modelMapper.map(u, UsuariosDTO.class))
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o e-mail fornecido."));
    }

    @Transactional
    public UsuariosDTO createUser(UsuariosDTO dto) {
        Usuarios usuarios = modelMapper.map(dto, Usuarios.class);
        Usuarios usuariosSaved = usuariosRepository.save(usuarios);
        return modelMapper.map(usuariosSaved, UsuariosDTO.class);
    }

    @Transactional
    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
        Usuarios existingUser = usuariosRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));

        existingUser.setUserName(dto.getUserName());
        existingUser.setEmail(dto.getEmail());
        Usuarios updatedUser = usuariosRepository.save(existingUser);

        return modelMapper.map(updatedUser, UsuariosDTO.class);
    }

    @Transactional
    public void deleteUsuarios(Long id) {
        if (!usuariosRepository.existsById(id)) {
            throw new UsuarioNotFoundException(String.format("Usuarios não encontrado com o id '%s'.", id));
        }
        usuariosRepository.deleteById(id);
    }

}