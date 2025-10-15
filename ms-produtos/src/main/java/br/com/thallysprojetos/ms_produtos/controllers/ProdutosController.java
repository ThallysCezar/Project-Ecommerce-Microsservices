package br.com.thallysprojetos.ms_produtos.controllers;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.responses.ProdutoResponse;
import br.com.thallysprojetos.ms_produtos.services.ProdutosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.Link;
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
    public ResponseEntity<ProdutoResponse> createProduct(@Valid @RequestBody ProdutosDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutosDTO createdProduct = service.createProduct(dto);
        
        ProdutoResponse response = new ProdutoResponse(
                createdProduct,
                "Produto adicionado com sucesso!"
        );
        
        // Link para o próximo passo: Criar pedido
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos", "criar-pedido")
                .withTitle("POST - Criar um pedido com os produtos"));
        
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProdutoResponse> createProducts(@Valid @RequestBody List<ProdutosDTO> dtos) {
        List<ProdutosDTO> createdProducts = service.createProducts(dtos);
        
        ProdutoResponse response = new ProdutoResponse(
                createdProducts,
                "Produtos adicionados com sucesso!"
        );
        
        // Link para o próximo passo: Criar pedido
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos", "criar-pedido")
                .withTitle("POST - Criar um pedido com os produtos"));
        
        return ResponseEntity.accepted().body(response);
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