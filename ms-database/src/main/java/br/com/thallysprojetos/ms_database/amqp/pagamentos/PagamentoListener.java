package br.com.thallysprojetos.ms_database.amqp.pagamentos;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_database.entities.Pagamento;
import br.com.thallysprojetos.ms_database.services.PagamentoPersistenceService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PagamentoListener {

    private final PagamentoPersistenceService produtosPersistenceService;
    private final ModelMapper modelMapper;

//    @RabbitListener(queues = "pagamentos.create.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
//    public void handleCreatePayment(PagamentoDTO dto) {
//        System.out.println("[PagamentoListener] Comando de CRIAÇÃO de Pagamento recebido: PedidoID=" + dto.getPedidoId());
//        try {
//            Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
//            produtosPersistenceService.save(pagamento);
//        } catch (Exception e) {
//            System.out.println("[PagamentoListener] ERRO ao processar CRIAÇÃO de Pagamento: " + e.getMessage());
//            throw e;
//        }
//    }

    @RabbitListener(queues = "pagamentos.update.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
    public void handleUpdatePayment(PagamentoDTO dto) {
        System.out.println("[PagamentoListener] Comando de ATUALIZAÇÃO de Pagamento recebido: PagamentoID=" + dto.getId());
        try {
            Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
            produtosPersistenceService.save(pagamento);
        } catch (Exception e) {
            System.out.println("[PagamentoListener] ERRO ao processar ATUALIZAÇÃO de Pagamento: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "pagamentos.delete.queue", containerFactory = "pagamentosRabbitListenerContainerFactory")
    public void handleDeletePayment(Long id) {
        System.out.println("[PagamentoListener] Comando de EXCLUSÃO de Pagamento recebido: PagamentoID=" + id);
        try {
            produtosPersistenceService.deleteById(id);
        } catch (Exception e) {
            System.out.println("[PagamentoListener] ERRO ao processar EXCLUSÃO de Pagamento: " + e.getMessage());
            throw e;
        }
    }

}