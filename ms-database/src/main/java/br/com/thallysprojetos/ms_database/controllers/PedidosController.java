package br.com.thallysprojetos.ms_database.controllers;

import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.services.PedidoPersistenceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidosController {

    private final PedidoPersistenceService service;

    // --- MÉTODOS DE LEITURA (Read Operations) ---

    @GetMapping
    public ResponseEntity<List<Pedidos>> findAll() {
        // Retorna a lista completa de entidades Pedidos
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedidos> findById(@PathVariable Long id) {
        // Retorna a entidade Pedidos ou 404
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<List<Pedidos>> findByUserId(@PathVariable Long usuarioId) {
        List<Pedidos> pedidos = service.findByUsuarioId(usuarioId);
        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedidos);
    }

}