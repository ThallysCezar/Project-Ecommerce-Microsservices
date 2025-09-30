package br.com.thallysprojetos.ms_database.amqp.pagamentos;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentosMQConfig {

    public static final String PAGAMENTOS_EXCHANGE = "pagamentos.exchange";
    public static final String PAGAMENTOS_QUEUE = "pagamentos.create.queue";
    public static final String PAGAMENTOS_ROUTING_KEY = "pagamentos.create";

    public static final String PAGAMENTOS_UPDATE_QUEUE = "pagamentos.update.queue";
    public static final String PAGAMENTOS_UPDATE_ROUTING_KEY = "pagamentos.update";

    public static final String PAGAMENTOS_DELETE_QUEUE = "pagamentos.delete.queue";
    public static final String PAGAMENTOS_DELETE_ROUTING_KEY = "pagamentos.delete";

    // 1. Declaração da Fila
    @Bean
    public Queue pagamentosQueue() {
        // A fila precisa ser durável para sobreviver a reinicializações do broker
        return new Queue(PAGAMENTOS_QUEUE, true);
    }

    // 2. Declaração do Exchange (o ponto de roteamento)
    @Bean
    public DirectExchange pagamentosExchange() {
        // Usamos DirectExchange porque o produtor enviará para uma chave específica
        return new DirectExchange(PAGAMENTOS_EXCHANGE);
    }

    // 3. Criação do Binding (Conexão entre a Fila e o Exchange)
    @Bean
    public Binding bindingPagamentos(Queue pagamentosQueue, DirectExchange pagamentosExchange) {
        // Conecta a fila ao exchange usando a chave de roteamento
        return BindingBuilder.bind(pagamentosQueue)
                .to(pagamentosExchange)
                .with(PAGAMENTOS_ROUTING_KEY);
    }

    @Bean
    public Queue pagamentosUpdateQueue() {
        return new Queue(PAGAMENTOS_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding bindingPagamentosUpdate(Queue pagamentosUpdateQueue, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosUpdateQueue)
                .to(pagamentosExchange)
                .with(PAGAMENTOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue pagamentosDeleteQueue() {
        return new Queue(PAGAMENTOS_DELETE_QUEUE, true);
    }

    @Bean
    public Binding bindingPagamentosDelete(Queue pagamentosDeleteQueue, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosDeleteQueue)
                .to(pagamentosExchange)
                .with(PAGAMENTOS_DELETE_ROUTING_KEY);
    }

}