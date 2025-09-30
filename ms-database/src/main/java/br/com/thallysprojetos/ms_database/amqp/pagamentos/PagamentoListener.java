package br.com.thallysprojetos.ms_database.amqp.pagamentos;

import br.com.thallysprojetos.ms_database.dtos.pagamentos.PagamentoDTO;
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

    // Listener para o comando de CRIAÇÃO
    @RabbitListener(queues = "pagamentos.create.queue")
    public void handleCreatePayment(PagamentoDTO dto) {
        System.out.println("Comando de CRIAÇÃO de Pagamento recebido: ID Pedido " + dto.getPedidoId());

        // Mapeia o DTO de entrada para a Entidade de domínio
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);

        // Persiste o novo pagamento no banco de dados
        produtosPersistenceService.save(pagamento);
    }

    // Listener para o comando de ATUALIZAÇÃO (usado também pelo processarPagamento)
    @RabbitListener(queues = "pagamentos.update.queue")
    public void handleUpdatePayment(PagamentoDTO dto) {
        // Nota: O DTO de entrada já deve ter o ID do pagamento a ser atualizado
        System.out.println("Comando de ATUALIZAÇÃO de Pagamento recebido: ID " + dto.getId());

        // Mapeia o DTO (que contém as alterações de status, valor, etc.)
        // A lógica do serviço de persistência cuida de buscar o original, aplicar mudanças e salvar.
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        produtosPersistenceService.save(pagamento);
    }

    // Listener para o comando de EXCLUSÃO
    @RabbitListener(queues = "pagamentos.delete.queue")
    public void handleDeletePayment(Long id) {
        System.out.println("Comando de EXCLUSÃO de Pagamento recebido: ID " + id);
        produtosPersistenceService.deleteById(id);
    }

}