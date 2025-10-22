package br.com.thallysprojetos.ms_produtos;

import br.com.thallysprojetos.ms_produtos.services.ProdutosServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({
		ProdutosServiceImplTest.class
})
@SuiteDisplayName("Conjunto dos testes unitários")
class ProdutosApplicationTests {
}