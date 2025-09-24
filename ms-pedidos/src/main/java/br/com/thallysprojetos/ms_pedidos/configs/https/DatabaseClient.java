package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.ms_pedidos.dtos.PedidosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    // Exemplo de como a API do ms-database seria chamada
    @GetMapping("/pedidos/{id}")
    PedidosDTO findPedidoById(@PathVariable("id") Long id);

    @PostMapping("/pedidos")
    PedidosDTO createPedido(@RequestBody PedidosDTO dto);

}