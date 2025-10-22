package br.com.thallysprojetos.ms_pedidos;

import br.com.thallysprojetos.ms_pedidos.service.PedidosServiceImplTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectClasses({
		PedidosServiceImplTest.class
})
@SuiteDisplayName("Conjunto dos testes unitários")
class PedidosApplicationTests {
}