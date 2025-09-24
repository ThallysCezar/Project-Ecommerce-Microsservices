package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.ms_pedidos.dtos.pagamentos.PagamentoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-pagamento")
public interface PagamentosClient {

    @GetMapping("/pagamentos/pedido/{idPedido}")
    PagamentoDTO findByPedidoId(@PathVariable Long idPedido);

    @PostMapping("/pagamentos")
    PagamentoDTO createPayment(@RequestBody PagamentoDTO dto);

}