package br.com.thallysprojetos.ms_database.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * Configuração de teste para fornecer mocks dos beans do RabbitMQ
 * quando o perfil "test" está ativo.
 * 
 * Isso permite que o contexto Spring carregue sem precisar de RabbitMQ real.
 */
@TestConfiguration
@Profile("test")
public class TestRabbitConfig {

    /**
     * Cria um mock do ConnectionFactory para evitar conexão com RabbitMQ.
     * Necessário para PagamentosMQConfig criar o SimpleRabbitListenerContainerFactory.
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        return mock(ConnectionFactory.class);
    }

    /**
     * Cria um mock do RabbitTemplate para evitar conexão com RabbitMQ durante os testes.
     * O PagamentoListener precisa deste bean para publicar mensagens.
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        return mock(RabbitTemplate.class);
    }

}