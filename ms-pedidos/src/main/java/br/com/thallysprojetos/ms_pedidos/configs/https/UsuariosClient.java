package br.com.thallysprojetos.ms_pedidos.configs.https;

import br.com.thallysprojetos.ms_pedidos.dtos.usuarios.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users")
public interface UsuariosClient {

    @GetMapping("/usuarios/{id}")
    UsuariosDTO findById(@PathVariable Long id);

}