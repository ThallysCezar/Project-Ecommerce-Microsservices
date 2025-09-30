package br.com.thallysprojetos.ms_database.controllers;

import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.entities.Produtos;
import br.com.thallysprojetos.ms_database.entities.Usuarios;
import br.com.thallysprojetos.ms_database.services.PedidoPersistenceService;
import br.com.thallysprojetos.ms_database.services.ProdutosPersistenceService;
import br.com.thallysprojetos.ms_database.services.UsuariosPersistenceService;
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
@RequestMapping("/produtos")
@AllArgsConstructor
public class ProdutosController {

    private final ProdutosPersistenceService service;

    // --- MÉTODOS DE LEITURA (Usados pelo FeignClient do ms-usuarios) ---

    @GetMapping
    public ResponseEntity<List<Produtos>> findAll() {
        // Retorna a Page<Entidade>
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produtos> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}