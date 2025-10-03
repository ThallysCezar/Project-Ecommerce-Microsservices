package br.com.thallysprojetos.ms_produtos.configs.http;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/produtos")
    List<ProdutosDTO> findAll();

    @GetMapping("/produtos/{id}")
    Optional<ProdutosDTO> findById(@PathVariable("id") Long id);

}