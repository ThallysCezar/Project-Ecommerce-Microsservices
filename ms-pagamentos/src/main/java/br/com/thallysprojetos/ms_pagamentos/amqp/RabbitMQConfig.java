package br.com.thallysprojetos.ms_pagamentos.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "pagamentos.exchange";
    public static final String CREATE_QUEUE = "pagamentos.create.queue";
    public static final String UPDATE_QUEUE = "pagamentos.update.queue";
    public static final String DELETE_QUEUE = "pagamentos.delete.queue";
    public static final String DLQ_CREATE = "pagamentos.create.dlq";
    public static final String DLQ_UPDATE = "pagamentos.update.dlq";
    public static final String DLQ_DELETE = "pagamentos.delete.dlq";

    @Bean
    public DirectExchange pagamentosExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue pagamentosCreateQueue() {
        return QueueBuilder.durable(CREATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_CREATE)
                .build();
    }

    @Bean
    public Queue pagamentosUpdateQueue() {
        return QueueBuilder.durable(UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_UPDATE)
                .build();
    }

    @Bean
    public Queue pagamentosDeleteQueue() {
        return QueueBuilder.durable(DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_DELETE)
                .build();
    }

    @Bean
    public Queue pagamentosCreateDLQ() {
        return QueueBuilder.durable(DLQ_CREATE).build();
    }

    @Bean
    public Queue pagamentosUpdateDLQ() {
        return QueueBuilder.durable(DLQ_UPDATE).build();
    }

    @Bean
    public Queue pagamentosDeleteDLQ() {
        return QueueBuilder.durable(DLQ_DELETE).build();
    }

    @Bean
    public Binding bindingCreate(Queue pagamentosCreateQueue, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosCreateQueue).to(pagamentosExchange).with("pagamentos.create");
    }

    @Bean
    public Binding bindingUpdate(Queue pagamentosUpdateQueue, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosUpdateQueue).to(pagamentosExchange).with("pagamentos.update");
    }

    @Bean
    public Binding bindingDelete(Queue pagamentosDeleteQueue, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosDeleteQueue).to(pagamentosExchange).with("pagamentos.delete");
    }

    @Bean
    public Binding bindingCreateDLQ(Queue pagamentosCreateDLQ, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosCreateDLQ).to(pagamentosExchange).with(DLQ_CREATE);
    }

    @Bean
    public Binding bindingUpdateDLQ(Queue pagamentosUpdateDLQ, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosUpdateDLQ).to(pagamentosExchange).with(DLQ_UPDATE);
    }

    @Bean
    public Binding bindingDeleteDLQ(Queue pagamentosDeleteDLQ, DirectExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentosDeleteDLQ).to(pagamentosExchange).with(DLQ_DELETE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory pagamentosListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}
