package br.com.thallysprojetos.ms_database.amqp.pagamentos;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentosMQConfig {

    public static final String PAGAMENTOS_CREATE_DLQ = "pagamentos.create.dlq";
    public static final String PAGAMENTOS_UPDATE_DLQ = "pagamentos.update.dlq";
    public static final String PAGAMENTOS_DELETE_DLQ = "pagamentos.delete.dlq";

    public static final String PAGAMENTOS_EXCHANGE = "pagamentos.exchange";
    public static final String PAGAMENTOS_QUEUE = "pagamentos.create.queue";
    public static final String PAGAMENTOS_ROUTING_KEY = "pagamentos.create";

    public static final String PAGAMENTOS_UPDATE_QUEUE = "pagamentos.update.queue";
    public static final String PAGAMENTOS_UPDATE_ROUTING_KEY = "pagamentos.update";

    public static final String PAGAMENTOS_DELETE_QUEUE = "pagamentos.delete.queue";
    public static final String PAGAMENTOS_DELETE_ROUTING_KEY = "pagamentos.delete";

    public static final String PAGAMENTOS_UPDATE_PEDIDO_QUEUE = "pedidos.update.pagamento.queue";
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PAGAMENTOS_UPDATE_PEDIDO_ROUTING_KEY = "pedidos.update.pagamento";

    @Bean(name = "pagamentosRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory pagamentosRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public Queue pagamentosQueue() {
        return QueueBuilder.durable(PAGAMENTOS_QUEUE)
                .withArgument("x-dead-letter-exchange", PAGAMENTOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAGAMENTOS_CREATE_DLQ)
                .build();
    }

    @Bean(name = "pagamentosExchange")
    public DirectExchange pagamentosExchange() {
        return ExchangeBuilder.directExchange(PAGAMENTOS_EXCHANGE).build();
    }

    @Bean
    public Binding bindingPagamentos(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosQueue())
                .to(pagamentosExchange)
                .with(PAGAMENTOS_ROUTING_KEY);
    }

    @Bean
    public Queue pagamentosUpdateQueue() {
        return QueueBuilder.durable(PAGAMENTOS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", PAGAMENTOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAGAMENTOS_UPDATE_DLQ)
                .build();
    }

    @Bean
    public Binding bindingPagamentosUpdate(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosUpdateQueue())
                .to(pagamentosExchange)
                .with(PAGAMENTOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue pagamentosDeleteQueue() {
        return QueueBuilder.durable(PAGAMENTOS_DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", PAGAMENTOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAGAMENTOS_DELETE_DLQ)
                .build();
    }

    @Bean
    public Binding bindingPagamentosDelete(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosDeleteQueue())
                .to(pagamentosExchange)
                .with(PAGAMENTOS_DELETE_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosUpdatePagamentoQueue() {
        return QueueBuilder.durable(PAGAMENTOS_UPDATE_PEDIDO_QUEUE).build();
    }

    @Bean(name = "pedidosExchangeFromPagamentos")
    public DirectExchange pedidosExchangeFromPagamentos() {
        return ExchangeBuilder.directExchange(PEDIDOS_EXCHANGE).build();
    }

    @Bean
    public Binding pedidosUpdatePagamentoBinding(DirectExchange pedidosExchangeFromPagamentos) {
        return BindingBuilder.bind(pedidosUpdatePagamentoQueue())
                .to(pedidosExchangeFromPagamentos)
                .with(PAGAMENTOS_UPDATE_PEDIDO_ROUTING_KEY);
    }

    @Bean
    public Queue pagamentosCreateDLQ() {
        return QueueBuilder.durable(PAGAMENTOS_CREATE_DLQ).build();
    }

    @Bean
    public Queue pagamentosUpdateDLQ() {
        return QueueBuilder.durable(PAGAMENTOS_UPDATE_DLQ).build();
    }

    @Bean
    public Queue pagamentosDeleteDLQ() {
        return QueueBuilder.durable(PAGAMENTOS_DELETE_DLQ).build();
    }

}