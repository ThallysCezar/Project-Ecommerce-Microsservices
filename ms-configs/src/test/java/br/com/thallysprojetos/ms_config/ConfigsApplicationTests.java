package br.com.thallysprojetos.ms_config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Corrigido: Apontando para a classe principal correta (ms_config com underscore)
// Por quê? O teste precisa saber qual é a classe @SpringBootApplication para inicializar o contexto
@SpringBootTest(classes = br.com.thallysprojetos.ms_config.ConfigsApplication.class)
class ConfigsApplicationTests {

	@Test
	void contextLoads() {
		// Este teste verifica se o contexto do Spring Boot carrega sem erros
		// É um teste básico mas importante para garantir que a configuração está correta
	}

}
