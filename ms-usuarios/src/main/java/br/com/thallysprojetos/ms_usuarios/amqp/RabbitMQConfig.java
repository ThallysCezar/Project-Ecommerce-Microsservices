package br.com.thallysprojetos.ms_usuarios.amqp;

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

//    @Bean(name = "usuariosRabbitListenerContainerFactory")
//    public org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory usuariosRabbitListenerContainerFactory(
//            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
//            org.springframework.amqp.support.converter.Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
//        org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory factory = new org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(jackson2JsonMessageConverter);
//        factory.setConcurrentConsumers(3);
//        factory.setMaxConcurrentConsumers(10);
//        return factory;
//    }

    
    public static final String USUARIOS_CREATE_QUEUE = "usuarios.create.queue";
    public static final String USUARIOS_CREATE_DLQ = "usuarios.create.dlq";
    public static final String USUARIOS_CREATE_ROUTING_KEY = "usuarios.create";

    public static final String USUARIOS_UPDATE_QUEUE = "usuarios.update.queue";
    public static final String USUARIOS_UPDATE_DLQ = "usuarios.update.dlq";
    public static final String USUARIOS_UPDATE_ROUTING_KEY = "usuarios.update";

    public static final String USUARIOS_DELETE_QUEUE = "usuarios.delete.queue";
    public static final String USUARIOS_DELETE_DLQ = "usuarios.delete.dlq";
    public static final String USUARIOS_DELETE_ROUTING_KEY = "usuarios.delete";

    public static final String USUARIOS_EXCHANGE = "usuarios.exchange";

    @Bean
    public DirectExchange usuariosExchange() {
        return ExchangeBuilder.directExchange(USUARIOS_EXCHANGE).build();
    }

    @Bean
    public Queue usuariosCreateQueue() {
        return QueueBuilder.durable(USUARIOS_CREATE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", USUARIOS_CREATE_DLQ)
            .build();
    }

    @Bean
    public Queue usuariosCreateDLQ() {
        return QueueBuilder.durable(USUARIOS_CREATE_DLQ).build();
    }

    @Bean
    public Binding bindingUsuariosCreate(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosCreateQueue())
            .to(usuariosExchange)
            .with(USUARIOS_CREATE_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosUpdateQueue() {
        return QueueBuilder.durable(USUARIOS_UPDATE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", USUARIOS_UPDATE_DLQ)
            .build();
    }

    @Bean
    public Queue usuariosUpdateDLQ() {
        return QueueBuilder.durable(USUARIOS_UPDATE_DLQ).build();
    }

    @Bean
    public Binding bindingUsuariosUpdate(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosUpdateQueue())
            .to(usuariosExchange)
            .with(USUARIOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosDeleteQueue() {
        return QueueBuilder.durable(USUARIOS_DELETE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", USUARIOS_DELETE_DLQ)
            .build();
    }

    @Bean
    public Queue usuariosDeleteDLQ() {
        return QueueBuilder.durable(USUARIOS_DELETE_DLQ).build();
    }

    @Bean
    public Binding bindingUsuariosDelete(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosDeleteQueue())
            .to(usuariosExchange)
            .with(USUARIOS_DELETE_ROUTING_KEY);
    }

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