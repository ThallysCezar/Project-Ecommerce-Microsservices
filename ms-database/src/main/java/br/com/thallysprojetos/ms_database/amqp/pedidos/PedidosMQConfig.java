package br.com.thallysprojetos.ms_database.amqp.pedidos;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidosMQConfig {

    public static final String PEDIDOS_CREATE_DLQ = "pedidos.create.dlq";
    public static final String PEDIDOS_UPDATE_DLQ = "pedidos.update.dlq";
    public static final String PEDIDOS_DELETE_DLQ = "pedidos.delete.dlq";

    public static final String PEDIDOS_CONFIRM_QUEUE = "pedidos.confirm.queue";
    public static final String PEDIDOS_CONFIRM_ROUTING_KEY = "pedidos.confirm";
    public static final String PEDIDOS_CONFIRM_DLQ = "pedidos.confirm.dlq";

    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_QUEUE = "pedidos.create.queue";
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.create";

    public static final String PEDIDOS_UPDATE_QUEUE = "pedidos.update.queue";
    public static final String PEDIDOS_UPDATE_ROUTING_KEY = "pedidos.update";

    public static final String PEDIDOS_DELETE_QUEUE = "pedidos.delete.queue";
    public static final String PEDIDOS_DELETE_ROUTING_KEY = "pedidos.delete";

    public static final String PEDIDOS_CANCEL_QUEUE = "pedidos.cancel.queue";
    public static final String PEDIDOS_DLQ_CANCEL = "pedidos.cancel.dlq";

    @Bean(name = "pedidosRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory pedidosRabbitListenerContainerFactory(
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
    public Queue pedidosQueue() {
    return QueueBuilder.durable(PEDIDOS_QUEUE)
        .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", PEDIDOS_CREATE_DLQ)
        .build();
    }

    @Bean
    public DirectExchange pedidosExchange() {
        return ExchangeBuilder.directExchange(PEDIDOS_EXCHANGE).build();
    }

    @Bean
    public Binding bindingPedidos(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosQueue())
                .to(pedidosExchange)
                .with(PEDIDOS_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosUpdateQueue() {
        return QueueBuilder.durable(PEDIDOS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_UPDATE_DLQ)
                .build();
    }

    @Bean
    public Binding bindingPedidosUpdate(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosUpdateQueue())
                .to(pedidosExchange)
                .with(PEDIDOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosDeleteQueue() {
        return QueueBuilder.durable(PEDIDOS_DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DELETE_DLQ)
                .build();
    }

    @Bean
    public Binding bindingPedidosDelete(Queue pedidosDeleteQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosDeleteQueue)
                .to(pedidosExchange)
                .with(PEDIDOS_DELETE_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosCreateDLQ() {
        return QueueBuilder.durable(PEDIDOS_CREATE_DLQ).build();
    }

    @Bean
    public Queue pedidosUpdateDLQ() {
        return QueueBuilder.durable(PEDIDOS_UPDATE_DLQ).build();
    }

    @Bean
    public Queue pedidosDeleteDLQ() {
        return QueueBuilder.durable(PEDIDOS_DELETE_DLQ).build();
    }

    @Bean
    public Queue pedidosConfirmQueue() {
        return QueueBuilder.durable(PEDIDOS_CONFIRM_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_CONFIRM_DLQ)
                .build();
    }

    @Bean
    public Queue pedidosConfirmDLQ() {
        return QueueBuilder.durable(PEDIDOS_CONFIRM_DLQ).build();
    }

    @Bean
    public Binding bindingPedidosConfirm(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosConfirmQueue())
                .to(pedidosExchange)
                .with(PEDIDOS_CONFIRM_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosCancelQueue() {
        return QueueBuilder.durable(PEDIDOS_CANCEL_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_CANCEL)
                .build();
    }

    @Bean
    public Queue pedidosCancelDLQ() {
        return QueueBuilder.durable(PEDIDOS_DLQ_CANCEL).build();
    }

    @Bean
    public Binding bindingCancel(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosCancelQueue()).to(pedidosExchange).with("pedidos.cancel");
    }

}