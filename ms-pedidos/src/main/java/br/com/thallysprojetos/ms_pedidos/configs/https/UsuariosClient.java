package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", path = "/usuarios")
public interface UsuariosClient {

    @GetMapping("/{id}")  // path já tem /usuarios
    UsuariosDTO findById(@PathVariable Long id);

    @GetMapping("/email/{email}")  // path já tem /usuarios
    UsuariosDTO findByEmail(@PathVariable String email);

}