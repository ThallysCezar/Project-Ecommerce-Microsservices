package br.com.thallysprojetos.ms_database.repositories;

import br.com.thallysprojetos.ms_usuarios.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByEmail(String email);

}