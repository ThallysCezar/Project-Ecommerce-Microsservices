package br.com.thallysprojetos.ms_database;

import br.com.thallysprojetos.ms_database.config.TestRabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DatabaseApplication.class)
@ActiveProfiles("test") // Usa application-test.properties com H2 em memória
@Import(TestRabbitConfig.class) // Importa configuração de teste com RabbitTemplate mockado
class DatabaseApplicationTests {

	@Test
	void contextLoads() {
	}

}
