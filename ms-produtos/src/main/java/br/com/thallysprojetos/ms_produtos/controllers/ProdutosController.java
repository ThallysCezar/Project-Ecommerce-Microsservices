package br.com.thallysprojetos.ms_produtos.controllers;

import br.com.thallysprojetos.ms_produtos.dtos.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.services.ProdutosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<ProdutosDTO> createProduct(@Valid @RequestBody ProdutosDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutosDTO product = service.createProduct(dto);
        URI endereco = uriBuilder.path("/produtos/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(endereco).body(product);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ProdutosDTO>> createProduct(@Valid @RequestBody List<ProdutosDTO> dtos, UriComponentsBuilder uriBuilder) {
        List<ProdutosDTO> produtos = service.createProducts(dtos);
        URI endereco = uriBuilder.path("/produtos/batch").build().toUri();
        return ResponseEntity.created(endereco).body(produtos);
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