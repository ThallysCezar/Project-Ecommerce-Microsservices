package br.com.thallysprojetos.ms_produtos.amqp;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidosAMQPConfigs {

//    @Bean
//    public Jackson2JsonMessageConverter messagemConvertida(){
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter);
//
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public Queue filaDetalhePedidos(){
//        return QueueBuilder.nonDurable("pagamentos.detalhes-pedido").build();
//    }
//
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return ExchangeBuilder.fanoutExchange("pagamentos.ex").build();
//    }
//
//    @Bean
//    public Binding bindingPagamentoPedido(FanoutExchange fanoutExchange){
//        return BindingBuilder.bind(filaDetalhePedidos()).to(fanoutExchange);
//    }
//
//
//    @Bean
//    public RabbitAdmin criarRabbitAdmin(ConnectionFactory conn){
//        return new RabbitAdmin(conn);
//    }
//
//    @Bean
//    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
//        return event -> rabbitAdmin.initialize();
//    }

}