package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.ms_database.entities.Usuarios;
import br.com.thallysprojetos.ms_database.repositories.UsuariosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuariosPersistenceService {

    private final UsuariosRepository usuariosRepository;

    public List<Usuarios> findAll() {
        return usuariosRepository.findAll();
    }

    public Optional<Usuarios> findById(Long id) {
        return usuariosRepository.findById(id);
    }

    public Optional<Usuarios> findByEmail(String email) {
        return usuariosRepository.findByEmail(email);
    }

    public boolean existsById(Long id) {
        return usuariosRepository.existsById(id);
    }

    @Transactional
    public Usuarios save(Usuarios usuario) {
        return usuariosRepository.save(usuario);
    }

//    @Transactional
//    public Usuarios update(Usuarios usuario) {
//        // Simplificação: o save() já faz o update se o ID estiver presente.
//        // O Listener deve buscar, atualizar o objeto e enviar para o save.
//        return usuariosRepository.save(usuario);
//    }

    @Transactional
    public void deleteById(Long id) {
        usuariosRepository.deleteById(id);
    }

}