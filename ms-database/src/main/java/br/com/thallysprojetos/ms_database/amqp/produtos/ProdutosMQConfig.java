package br.com.thallysprojetos.ms_database.amqp.produtos;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutosMQConfig {

    public static final String PRODUTOS_EXCHANGE = "produtos.exchange";
    public static final String PRODUTOS_QUEUE = "produtos.create.queue";
    public static final String PRODUTOS_ROUTING_KEY = "produtos.create";

    public static final String PRODUTOS_UPDATE_QUEUE = "produtos.update.queue";
    public static final String PRODUTOS_UPDATE_ROUTING_KEY = "produtos.update";

    public static final String PRODUTOS_DELETE_QUEUE = "produtos.delete.queue";
    public static final String PRODUTOS_DELETE_ROUTING_KEY = "produtos.delete";

    @Bean
    public Queue produtosQueue() {
        return new Queue(PRODUTOS_QUEUE, true);
    }

    @Bean
    public DirectExchange produtosExchange() {
        return new DirectExchange(PRODUTOS_EXCHANGE);
    }

    @Bean
    public Binding bindingProdutos(Queue produtosQueue, DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosQueue)
                .to(produtosExchange)
                .with(PRODUTOS_ROUTING_KEY);
    }

    @Bean
    public Queue produtosUpdateQueue() {
        return new Queue(PRODUTOS_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding bindingProdutosUpdate(Queue produtosUpdateQueue, DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosUpdateQueue)
                .to(produtosExchange)
                .with(PRODUTOS_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Queue produtosDeleteQueue() {
        return new Queue(PRODUTOS_DELETE_QUEUE, true);
    }

    @Bean
    public Binding bindingProdutosDelete(Queue produtosDeleteQueue, DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosDeleteQueue)
                .to(produtosExchange)
                .with(PRODUTOS_DELETE_ROUTING_KEY);
    }

}
