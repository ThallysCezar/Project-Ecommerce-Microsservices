package br.com.thallysprojetos.ms_database.amqp.usuarios;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuariosMQConfig {

    public static final String USUARIOS_CREATE_DLQ = "usuarios.create.dlq";
    public static final String USUARIOS_UPDATE_DLQ = "usuarios.update.dlq";
    public static final String USUARIOS_DELETE_DLQ = "usuarios.delete.dlq";

    public static final String USUARIOS_EXCHANGE = "usuarios.exchange";
    public static final String USUARIOS_QUEUE = "usuarios.create.queue";
    public static final String USUARIOS_ROUTING_KEY = "usuarios.create";

    public static final String USUARIOS_UPDATE_QUEUE = "usuarios.update.queue";
    public static final String USUARIOS_UPDATE_ROUTING_KEY = "usuarios.update";

    public static final String USUARIOS_DELETE_QUEUE = "usuarios.delete.queue";
    public static final String USUARIOS_DELETE_ROUTING_KEY = "usuarios.delete";

    @Bean(name = "usuariosRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory usuariosRabbitListenerContainerFactory(
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
    public Queue usuariosQueue() {
        return QueueBuilder.durable(USUARIOS_QUEUE)
                .withArgument("x-dead-letter-exchange", USUARIOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USUARIOS_CREATE_DLQ)
                .build();
    }

    @Bean(name = "usuariosExchange")
    public DirectExchange usuariosExchange() {
        return ExchangeBuilder.directExchange(USUARIOS_EXCHANGE).build();
    }

    @Bean
    public Binding bindingUsuarios(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosQueue()).to(usuariosExchange).with(USUARIOS_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosUpdateQueue() {
        return QueueBuilder.durable(USUARIOS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", USUARIOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USUARIOS_UPDATE_DLQ)
                .build();
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
                .withArgument("x-dead-letter-exchange", USUARIOS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USUARIOS_DELETE_DLQ)
                .build();
    }

    @Bean
    public Binding bindingUsuariosDelete(Queue usuariosDeleteQueue, DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosDeleteQueue())
                .to(usuariosExchange)
                .with(USUARIOS_DELETE_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosCreateDLQ() {
        return QueueBuilder.durable(USUARIOS_CREATE_DLQ).build();
    }

    @Bean
    public Queue usuariosUpdateDLQ() {
        return QueueBuilder.durable(USUARIOS_UPDATE_DLQ).build();
    }

    @Bean
    public Queue usuariosDeleteDLQ() {
        return QueueBuilder.durable(USUARIOS_DELETE_DLQ).build();
    }

}