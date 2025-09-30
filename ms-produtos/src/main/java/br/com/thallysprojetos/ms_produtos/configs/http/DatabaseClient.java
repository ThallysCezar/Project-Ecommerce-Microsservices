package br.com.thallysprojetos.ms_produtos.configs.http;

import br.com.thallysprojetos.ms_produtos.dtos.ProdutosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/produtos")
    List<ProdutosDTO> findAll();

    @GetMapping("/produtos/{id}")
    Optional<ProdutosDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/produtos/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

    @PostMapping("/produtos")
    ProdutosDTO createProduto(@RequestBody ProdutosDTO dto);

    @PutMapping("/produtos/{id}")
    ProdutosDTO updateProduto(@PathVariable("id") Long id, @RequestBody ProdutosDTO dto);

    @DeleteMapping("/produtos/{id}")
    void deleteProduto(@PathVariable("id") Long id);

}