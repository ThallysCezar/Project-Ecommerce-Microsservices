package br.com.thallysprojetos.ms_database.amqp.pagamentos;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PagamentoPedidoUpdateDTO;
import br.com.thallysprojetos.ms_database.entities.Pagamento;
import br.com.thallysprojetos.ms_database.services.PagamentoPersistenceService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PagamentoListener {

    private final PagamentoPersistenceService produtosPersistenceService;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "pagamentos.create.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
    public void handleCreatePayment(PagamentoDTO dto) {
        System.out.println("[DATABASE] Comando de CRIAÇÃO de Pagamento recebido: PedidoID=" + dto.getPedidoId());
        try {
            Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
            Pagamento saved = produtosPersistenceService.save(pagamento);

            PagamentoPedidoUpdateDTO updateDTO = new PagamentoPedidoUpdateDTO(saved.getPedidoId(), saved.getId());
            rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.update.pagamento", updateDTO);
            if (saved.getStatus() != null && saved.getStatus().name().equalsIgnoreCase("CONFIRMADO")) {
                System.out.println("[DATABASE] Pagamento confirmado, publicando evento para pedidos.confirm.queue: PedidoID=" + saved.getPedidoId());
                rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.confirm", saved.getPedidoId());
            }
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar CRIAÇÃO de Pagamento: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pagamentos.update.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
    public void handleUpdatePayment(PagamentoDTO dto) {
        System.out.println("[DATABASE] Comando de ATUALIZAÇÃO de Pagamento recebido: PagamentoID=" + dto.getId());
        try {
            Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
            produtosPersistenceService.save(pagamento);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar ATUALIZAÇÃO de Pagamento: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pagamentos.delete.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
    public void handleDeletePayment(Long id) {
        System.out.println("[DATABASE] Comando de EXCLUSÃO de Pagamento recebido: PagamentoID=" + id);
        try {
            produtosPersistenceService.deleteById(id);
        } catch (Exception e) {
            System.out.println("[DATABASE] ERRO ao processar EXCLUSÃO de Pagamento: " + e.getMessage());
            throw e;
        }
    }

}