package br.com.thallysprojetos.ms_produtos.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String PRODUTOS_CREATE_QUEUE = "produtos.create.queue";
    public static final String PRODUTOS_CREATE_DLQ = "produtos.create.dlq";
    public static final String PRODUTOS_CREATE_ROUTING_KEY = "produtos.create";

    public static final String PRODUTOS_UPDATE_QUEUE = "produtos.update.queue";
    public static final String PRODUTOS_UPDATE_DLQ = "produtos.update.dlq";
    public static final String PRODUTOS_UPDATE_ROUTING_KEY = "produtos.update";

    public static final String PRODUTOS_DELETE_QUEUE = "produtos.delete.queue";
    public static final String PRODUTOS_DELETE_DLQ = "produtos.delete.dlq";
    public static final String PRODUTOS_DELETE_ROUTING_KEY = "produtos.delete";

    @Bean
    public DirectExchange produtosExchange() {
        return ExchangeBuilder.directExchange(PRODUTOS_EXCHANGE).build();
    }

    @Bean
    public Queue produtosCreateQueue() {
        return QueueBuilder.durable(PRODUTOS_CREATE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", PRODUTOS_CREATE_DLQ)
            .build();
    }

    @Bean
    public Queue produtosCreateDLQ() {
        return QueueBuilder.durable(PRODUTOS_CREATE_DLQ).build();
    }

    @Bean
    public Binding bindingProdutosCreate(DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosCreateQueue())
            .to(produtosExchange)
            .with(PRODUTOS_CREATE_ROUTING_KEY);
    }

    @Bean
    public Queue produtosUpdateQueue() {
        return QueueBuilder.durable(PRODUTOS_UPDATE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", PRODUTOS_UPDATE_DLQ)
            .build();
    }

    @Bean
    public Queue produtosUpdateDLQ() {
        return QueueBuilder.durable(PRODUTOS_UPDATE_DLQ).build();
    }

    @Bean
    public Binding bindingProdutosUpdate(DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosUpdateQueue())
            .to(produtosExchange)
            .with(PRODUTOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue produtosDeleteQueue() {
        return QueueBuilder.durable(PRODUTOS_DELETE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", PRODUTOS_DELETE_DLQ)
            .build();
    }

    @Bean
    public Queue produtosDeleteDLQ() {
        return QueueBuilder.durable(PRODUTOS_DELETE_DLQ).build();
    }

    @Bean
    public Binding bindingProdutosDelete(DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosDeleteQueue())
            .to(produtosExchange)
            .with(PRODUTOS_DELETE_ROUTING_KEY);
    }

    public static final String PRODUTOS_EXCHANGE = "produtos.exchange";

    @Bean
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory conn){
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messagemConvertida(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

}