package br.com.thallysprojetos.ms_produtos.controllers;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.services.ProdutosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@AllArgsConstructor
public class ProdutosController {

    private final ProdutosService service;

    @GetMapping
    public ResponseEntity<List<ProdutosDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutosDTO> findProductById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findProductById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProdutosDTO dto, UriComponentsBuilder uriBuilder) {
        service.createProduct(dto);
        return ResponseEntity.accepted().body("Produto adicionado com sucesso!");
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createProduct(@Valid @RequestBody List<ProdutosDTO> dtos) {
        service.createProducts(dtos);
        return ResponseEntity.accepted().body("Produtos adicionados com sucesso!");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProdutosDTO> updateProduct(@Valid @PathVariable Long id, @RequestBody ProdutosDTO dto) {
        service.updateProdutos(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@Valid @PathVariable Long id) {
        service.deleteProdutos(id);
    }

}