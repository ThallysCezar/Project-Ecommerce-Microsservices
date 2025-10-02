package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pagamento")
public interface PagamentosClient {

    @GetMapping("/pagamentos/pedido/{idPedido}")
    PagamentoDTO findByPedidoId(@PathVariable Long idPedido);

    @GetMapping("/pagamentos/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    // ACREDITO QUE NÃO ESTEJA SENDO UTILIZADO, TESTAR A POSSIBILIDADE DE APAGAR.
}