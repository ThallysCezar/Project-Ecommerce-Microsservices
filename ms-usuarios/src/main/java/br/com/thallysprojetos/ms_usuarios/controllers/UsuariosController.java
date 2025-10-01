package br.com.thallysprojetos.ms_usuarios.controllers;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.services.UsuariosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuariosController {

    private final UsuariosService service;

    @GetMapping
    public ResponseEntity<List<UsuariosDTO>> findAll() { // <--- Retorna List<DTO>
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuariosDTO> findUserById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuariosDTO> findUserByEmail(@Valid @PathVariable String email) {
        return ResponseEntity.ok().body(service.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UsuariosDTO dto) {
        service.createUser(dto);
        return ResponseEntity.accepted().body("Usuário recebido e será criado em breve (processamento assíncrono via RabbitMQ).");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UsuariosDTO> updateUser(@Valid @PathVariable Long id, @RequestBody UsuariosDTO dto) {
        service.updateUsuarios(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable Long id) {
        service.deleteUsuarios(id);
    }

}