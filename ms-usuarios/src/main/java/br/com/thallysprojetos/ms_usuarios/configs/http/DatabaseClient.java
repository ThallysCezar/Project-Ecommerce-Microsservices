package br.com.thallysprojetos.ms_usuarios.configs.http;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/usuarios")
    List<UsuariosDTO> findAll();

    @GetMapping("/usuarios/{id}")
    Optional<UsuariosDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/usuarios/email/{email}")
    Optional<UsuariosDTO> findByEmail(@PathVariable("email") String email);

    @GetMapping("/usuarios/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

}