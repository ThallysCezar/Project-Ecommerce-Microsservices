package br.com.thallysprojetos.ms_database.amqp.pedidos;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidosMQConfig {

    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_QUEUE = "pedidos.create.queue";
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.create";

    public static final String PEDIDOS_UPDATE_QUEUE = "pedidos.update.queue";
    public static final String PEDIDOS_UPDATE_ROUTING_KEY = "pedidos.update";

    public static final String PEDIDOS_DELETE_QUEUE = "pedidos.delete.queue";
    public static final String PEDIDOS_DELETE_ROUTING_KEY = "pedidos.delete";

    @Bean
    public Queue pedidosQueue() {
        return new Queue(PEDIDOS_QUEUE, true);
    }

    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(PEDIDOS_EXCHANGE);
    }

    @Bean
    public Binding bindingPedidos(Queue pedidosQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosQueue)
                .to(pedidosExchange)
                .with(PEDIDOS_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosUpdateQueue() {
        return new Queue(PEDIDOS_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding bindingPedidosUpdate(Queue pedidosUpdateQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosUpdateQueue)
                .to(pedidosExchange)
                .with(PEDIDOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosDeleteQueue() {
        return new Queue(PEDIDOS_DELETE_QUEUE, true);
    }

    @Bean
    public Binding bindingPedidosDelete(Queue pedidosDeleteQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosDeleteQueue)
                .to(pedidosExchange)
                .with(PEDIDOS_DELETE_ROUTING_KEY);
    }

}