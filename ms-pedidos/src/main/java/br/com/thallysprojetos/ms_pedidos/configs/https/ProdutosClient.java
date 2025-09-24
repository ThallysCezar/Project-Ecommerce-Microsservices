package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.ms_pedidos.dtos.produtos.ProdutosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-produtos")
public interface ProdutosClient {

    @GetMapping("/produtos/{id}")
    ProdutosDTO findById(@PathVariable Long id);

}