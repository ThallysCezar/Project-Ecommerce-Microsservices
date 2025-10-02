package br.com.thallysprojetos.ms_database.amqp.pedidos;

import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.services.PedidoPersistenceService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PedidosListener {
    @RabbitListener(queues = "pedidos.update.pagamento.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleUpdatePagamentoPedido(br.com.thallysprojetos.common_dtos.pedido.PagamentoPedidoUpdateDTO dto) {
        System.out.println("[DATABASE] Atualizando pedido com pagamento: PedidoID=" + dto.getPedidoId() + ", PagamentoID=" + dto.getPagamentoId());
        try {
            var pedidoOpt = pedidoPersistenceService.findById(dto.getPedidoId());
            if (pedidoOpt.isPresent()) {
                var pedido = pedidoOpt.get();
                pedido.setPagamentoId(dto.getPagamentoId());
                pedidoPersistenceService.save(pedido);
            } else {
                System.out.println("[DATABASE] Pedido não encontrado para atualização de pagamento: PedidoID=" + dto.getPedidoId());
            }
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao atualizar pagamento do pedido: " + e.getMessage());
            throw e;
        }
    }

    private final PedidoPersistenceService pedidoPersistenceService;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "pedidos.create.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleCreatePedido(PedidosDTO dto) {
        System.out.println("[DATABASE] Comando de CRIAÇÃO de Pedido recebido: PedidoID=" + dto.getId());
        try {
            Pedidos pedidos = modelMapper.map(dto, Pedidos.class);
            if (pedidos.getItens() != null) {
                for (var item : pedidos.getItens()) {
                    item.setPedido(pedidos);
                }
            }
            pedidoPersistenceService.save(pedidos);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar CRIAÇÃO de Pedido: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pedidos.update.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleUpdatePedido(PedidosDTO dto) {
        System.out.println("[DATABASE] Comando de ATUALIZAÇÃO de Pedido recebido: PedidoID=" + dto.getId());
        try {
            Pedidos pedidos = modelMapper.map(dto, Pedidos.class);
            pedidoPersistenceService.save(pedidos);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar ATUALIZAÇÃO de Pedido: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pedidos.delete.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleDeletePedido(Long id) {
        System.out.println("[DATABASE] Comando de EXCLUSÃO de Pedido recebido: PedidoID=" + id);
        try {
            pedidoPersistenceService.deleteById(id);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar EXCLUSÃO de Pedido: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pedidos.confirm.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleConfirmPedido(Long id) {
        System.out.println("[DATABASE] Comando de CONFIRMAÇÃO de Pedido recebido: PedidoID=" + id);
        try {
            pedidoPersistenceService.confirmarPedidos(id);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar CONFIRMAÇÃO de Pedido: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pedidos.cancel.queue", containerFactory = "pedidosRabbitListenerContainerFactory")
    public void handleCancelPedido(Long id) {
        System.out.println("[DATABASE] Comando de CANCELAMENTO de Pedido recebido: PedidoID=" + id);
        try {
            pedidoPersistenceService.cancelarPedido(id);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar CANCELAMENTO de Pedido: " + e.getMessage());
            throw e;
        }
    }

}