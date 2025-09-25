package br.com.thallysprojetos.ms_usuarios.configs.http;

import br.com.thallysprojetos.ms_usuarios.dtos.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/produtos")
    Page<UsuariosDTO> findAll(Pageable page);

    @GetMapping("/produtos/{id}")
    Optional<UsuariosDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/produtos/{email}")
    Optional<UsuariosDTO> findByEmail(@PathVariable("email") String email);

    @GetMapping("/produtos/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

    @PostMapping("/produtos")
    UsuariosDTO createUsuarios(@RequestBody UsuariosDTO dto);

    @PutMapping("/produtos/{id}")
    UsuariosDTO updateUsuarios(@PathVariable("id") Long id, @RequestBody UsuariosDTO dto);

    @DeleteMapping("/produtos/{id}")
    void deleteUsuarios(@PathVariable("id") Long id);
    
}