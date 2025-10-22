package br.com.thallysprojetos.ms_usuarios;

import br.com.thallysprojetos.ms_usuarios.services.AuthenticationServiceImplTest;
import br.com.thallysprojetos.ms_usuarios.services.UsuariosServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({
		AuthenticationServiceImplTest.class,
		UsuariosServiceImplTest.class
})
@SuiteDisplayName("Conjunto dos testes unitários")
class UsuariosApplicationTests {
}
