package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/pedidos")
    List<PedidosDTO> findAll();

    @GetMapping("/pedidos/{id}")
    Optional<PedidosDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/pedidos/usuarios/id")
    List<PedidosDTO> findByUsuarioId(@PathVariable("id") Long id);

    @GetMapping("/pedidos/usuarios/{usuarioId}/pendentes")
    List<PedidosDTO> findPendingByUsuarioId(@PathVariable("usuarioId") Long usuarioId);

}