package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.ms_pedidos.dtos.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.ProdutoIdDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.pagamentos.PagamentoDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.usuarios.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/pedidos")
    List<PedidosDTO> findAll();

    @GetMapping("/pedidos/{id}")
    Optional<PedidosDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/pedidos/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    @GetMapping("/pedidos/usuarios/id")
    List<PedidosDTO> findByUsuarioId(@PathVariable("id") Long id);

    @PostMapping("/pedidos")
    PedidosDTO createPedido(@RequestBody PedidosDTO dto);

    @GetMapping("/pedidos")
    Page<PedidosDTO> findAllPedidos(Pageable pageable);

    @GetMapping("/pedidos/{id}")
    PedidosDTO findPedidoById(@PathVariable("id") Long id);

    @GetMapping("/pedidos/user/{id}")
    List<PedidosDTO> findPedidosByUserId(@PathVariable("id") Long id);

    @PutMapping("/pedidos/{id}")
    PedidosDTO updatePedido(@PathVariable("id") Long id, @RequestBody PedidosDTO dto);

    @DeleteMapping("/pedidos/{id}")
    void deletePedido(@PathVariable("id") Long id);

    // Endpoints para a entidade Usuarios
    @GetMapping("/usuarios/{id}")
    UsuariosDTO findUsuarioById(@PathVariable("id") Long id);

    // Endpoints para a entidade Produtos
    @GetMapping("/produtos/{id}")
    ProdutoIdDTO findProdutoById(@PathVariable("id") Long id);

    // Endpoints para a entidade Pagamento
    @GetMapping("/pagamentos/pedido/{idPedido}")
    PagamentoDTO findPagamentoByPedidoId(@PathVariable("idPedido") Long idPedido);

    @PostMapping("/pagamentos")
    PagamentoDTO createPagamento(@RequestBody PagamentoDTO dto);

}