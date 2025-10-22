package br.com.thallysprojetos.ms_pagamentos;

import br.com.thallysprojetos.ms_pagamentos.services.PagamentoServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({
		PagamentoServiceImplTest.class
})
@SuiteDisplayName("Conjunto dos testes unitários")
class PagamentosApplicationTests {
}