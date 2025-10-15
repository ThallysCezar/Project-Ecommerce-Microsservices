package br.com.thallysprojetos.ms_usuarios.controllers;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.responses.UsuarioResponse;
import br.com.thallysprojetos.ms_usuarios.services.UsuariosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<UsuarioResponse> createUser(@Valid @RequestBody UsuariosDTO dto) {
        UsuariosDTO createdUser = service.createUser(dto);
        
        UsuarioResponse response = new UsuarioResponse(
                createdUser, 
                "Usuário recebido e será criado em breve (processamento assíncrono via RabbitMQ)."
        );
        
        // Links para o próximo passo: Criar produtos
        response.add(Link.of("http://localhost:8082/ms-produtos/produtos", "criar-produto-unico")
                .withTitle("POST - Criar um único produto"));
        response.add(Link.of("http://localhost:8082/ms-produtos/produtos/batch", "criar-produtos-lote")
                .withTitle("POST - Criar múltiplos produtos de uma vez"));
        response.add(Link.of("http://localhost:8082/ms-produtos/produtos", "listar-produtos")
                .withTitle("GET - Ver todos os produtos disponíveis"));
        
        return ResponseEntity.accepted().body(response);
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