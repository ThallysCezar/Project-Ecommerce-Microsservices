package br.com.thallysprojetos.ms_database.amqp.usuarios;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuariosMQConfig {

    public static final String USUARIOS_EXCHANGE = "usuarios.exchange";
    public static final String USUARIOS_QUEUE = "usuarios.create.queue";
    public static final String USUARIOS_ROUTING_KEY = "usuarios.create";

    public static final String USUARIOS_UPDATE_QUEUE = "usuarios.update.queue";
    public static final String USUARIOS_UPDATE_ROUTING_KEY = "usuarios.update";

    public static final String USUARIOS_DELETE_QUEUE = "usuarios.delete.queue";
    public static final String USUARIOS_DELETE_ROUTING_KEY = "usuarios.delete";

    @Bean
    public RabbitAdmin criarRabbitAdmin(org.springframework.amqp.rabbit.connection.ConnectionFactory conn){
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Queue usuariosQueue() {
        return QueueBuilder.nonDurable(USUARIOS_QUEUE).build();
    }

    @Bean
    public DirectExchange usuariosExchange() {
        return ExchangeBuilder.directExchange(USUARIOS_EXCHANGE).build();
    }

    @Bean
    public Binding bindingUsuarios(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosQueue()).to(usuariosExchange).with(USUARIOS_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosUpdateQueue() {
        return QueueBuilder.nonDurable(USUARIOS_UPDATE_QUEUE).build();
    }

    @Bean
    public Binding bindingUsuariosUpdate(DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosUpdateQueue())
                .to(usuariosExchange)
                .with(USUARIOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue usuariosDeleteQueue() {
        return QueueBuilder.nonDurable(USUARIOS_DELETE_QUEUE).build();
    }

    @Bean
    public Binding bindingUsuariosDelete(Queue usuariosDeleteQueue, DirectExchange usuariosExchange) {
        return BindingBuilder.bind(usuariosDeleteQueue())
                .to(usuariosExchange)
                .with(USUARIOS_DELETE_ROUTING_KEY);
    }

}