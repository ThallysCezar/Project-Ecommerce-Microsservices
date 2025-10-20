package br.com.thallysprojetos.ms_pedidos.service;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;

import java.util.List;

public interface PedidosService {

    List<PedidosDTO> findAll();

    PedidosDTO findById(Long id);

    List<PedidosDTO> findByUserId(Long id);

    PedidosDTO createPedido(PedidosDTO dto);

    void processarPagamentoDoPedido(Long idPedido, PagamentoDTO pagamentoDto);

    void updatePedidos(Long id, PedidosDTO dto);

    void deletePedidos(Long id);

    void confirmarPedidos(Long id);

    void cancelarPedido(Long id);

}