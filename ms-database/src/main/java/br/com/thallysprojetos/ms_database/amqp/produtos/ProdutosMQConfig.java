package br.com.thallysprojetos.ms_database.amqp.produtos;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutosMQConfig {
    public static final String PRODUTOS_CREATE_DLQ = "produtos.create.dlq";
    public static final String PRODUTOS_UPDATE_DLQ = "produtos.update.dlq";
    public static final String PRODUTOS_DELETE_DLQ = "produtos.delete.dlq";

    public static final String PRODUTOS_EXCHANGE = "produtos.exchange";
    public static final String PRODUTOS_QUEUE = "produtos.create.queue";
    public static final String PRODUTOS_ROUTING_KEY = "produtos.create";

    public static final String PRODUTOS_UPDATE_QUEUE = "produtos.update.queue";
    public static final String PRODUTOS_UPDATE_ROUTING_KEY = "produtos.update";

    public static final String PRODUTOS_DELETE_QUEUE = "produtos.delete.queue";
    public static final String PRODUTOS_DELETE_ROUTING_KEY = "produtos.delete";

    @Bean(name = "produtosRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory produtosRabbitListenerContainerFactory(
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
    public Queue produtosQueue() {
        return QueueBuilder.durable(PRODUTOS_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PRODUTOS_CREATE_DLQ)
                .build();
    }

    @Bean
    public DirectExchange produtosExchange() {
        return ExchangeBuilder.directExchange(PRODUTOS_EXCHANGE).build();
    }

    @Bean
    public Binding bindingProdutos(DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosQueue())
                .to(produtosExchange)
                .with(PRODUTOS_ROUTING_KEY);
    }

    @Bean
    public Queue produtosUpdateQueue() {
        return QueueBuilder.durable(PRODUTOS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PRODUTOS_UPDATE_DLQ)
                .build();
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
    public Binding bindingProdutosDelete(DirectExchange produtosExchange) {
        return BindingBuilder.bind(produtosDeleteQueue())
                .to(produtosExchange)
                .with(PRODUTOS_DELETE_ROUTING_KEY);
    }

    @Bean
    public Queue produtosCreateDLQ() {
        return QueueBuilder.durable(PRODUTOS_CREATE_DLQ).build();
    }

    @Bean
    public Queue produtosUpdateDLQ() {
        return QueueBuilder.durable(PRODUTOS_UPDATE_DLQ).build();
    }

    @Bean
    public Queue produtosDeleteDLQ() {
        return QueueBuilder.durable(PRODUTOS_DELETE_DLQ).build();
    }

}