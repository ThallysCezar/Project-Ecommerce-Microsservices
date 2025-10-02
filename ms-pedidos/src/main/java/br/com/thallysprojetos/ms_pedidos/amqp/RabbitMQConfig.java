package br.com.thallysprojetos.ms_pedidos.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_CREATE_QUEUE = "pedidos.create.queue";
    public static final String PEDIDOS_UPDATE_QUEUE = "pedidos.update.queue";
    public static final String PEDIDOS_DELETE_QUEUE = "pedidos.delete.queue";
    public static final String PEDIDOS_CONFIRM_QUEUE = "pedidos.confirm.queue";
    public static final String PEDIDOS_DLQ_CREATE = "pedidos.create.dlq";
    public static final String PEDIDOS_DLQ_UPDATE = "pedidos.update.dlq";
    public static final String PEDIDOS_DLQ_DELETE = "pedidos.delete.dlq";
    public static final String PEDIDOS_DLQ_CONFIRM = "pedidos.confirm.dlq";

    @Bean
    public DirectExchange pedidosExchange() {
        return ExchangeBuilder.directExchange(PEDIDOS_EXCHANGE).build();
    }

    @Bean
    public Queue pedidosCreateQueue() {
        return QueueBuilder.durable(PEDIDOS_CREATE_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_CREATE)
                .build();
    }

    @Bean
    public Queue pedidosCreateDLQ() {
        return QueueBuilder.durable(PEDIDOS_DLQ_CREATE).build();
    }

    @Bean
    public Binding bindingCreate(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosCreateQueue()).to(pedidosExchange).with("pedidos.create");
    }

    @Bean
    public Queue pedidosUpdateQueue() {
        return QueueBuilder.durable(PEDIDOS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_UPDATE)
                .build();
    }

    @Bean
    public Queue pedidosUpdateDLQ() {
        return QueueBuilder.durable(PEDIDOS_DLQ_UPDATE).build();
    }

    @Bean
    public Binding bindingUpdate(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosUpdateQueue()).to(pedidosExchange).with("pedidos.update");
    }

    @Bean
    public Queue pedidosDeleteQueue() {
        return QueueBuilder.durable(PEDIDOS_DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_DELETE)
                .build();
    }

    @Bean
    public Queue pedidosDeleteDLQ() {
        return QueueBuilder.durable(PEDIDOS_DLQ_DELETE).build();
    }

    @Bean
    public Binding bindingDelete(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosDeleteQueue()).to(pedidosExchange).with("pedidos.delete");
    }

    @Bean
    public Queue pedidosConfirmQueue() {
        return QueueBuilder.durable(PEDIDOS_CONFIRM_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_CONFIRM)
                .build();
    }

    @Bean
    public Queue pedidosConfirmDLQ() {
        return QueueBuilder.durable(PEDIDOS_DLQ_CONFIRM).build();
    }

    @Bean
    public Binding bindingConfirm(DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosConfirmQueue()).to(pedidosExchange).with("pedidos.confirm");
    }

    @Bean
    public Binding bindingCreateDLQ(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pedidosCreateDLQ()).to(pagamentosExchange).with(PEDIDOS_DLQ_CREATE);
    }

    @Bean
    public Binding bindingUpdateDLQ(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pedidosUpdateDLQ()).to(pagamentosExchange).with(PEDIDOS_DLQ_UPDATE);
    }

    @Bean
    public Binding bindingDeleteDLQ(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pedidosDeleteDLQ()).to(pagamentosExchange).with(PEDIDOS_DLQ_DELETE);
    }

    @Bean
    public Binding bindingConfirmDLQ(DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pedidosConfirmDLQ()).to(pagamentosExchange).with(PEDIDOS_DLQ_CONFIRM);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory pedidosListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }

}