package br.com.thallysprojetos.ms_usuarios.configs.http;

import br.com.thallysprojetos.ms_usuarios.CustomPageImpl;
import br.com.thallysprojetos.ms_usuarios.dtos.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/usuarios")
    UsuariosDTO createUsuarios(@RequestBody UsuariosDTO dto);

    @PutMapping("/usuarios/{id}")
    UsuariosDTO updateUsuarios(@PathVariable("id") Long id, @RequestBody UsuariosDTO dto);

    @DeleteMapping("/usuarios/{id}")
    void deleteUsuarios(@PathVariable("id") Long id);
    
}