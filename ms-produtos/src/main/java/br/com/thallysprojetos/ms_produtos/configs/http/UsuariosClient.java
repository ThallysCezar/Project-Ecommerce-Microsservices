package br.com.thallysprojetos.ms_produtos.configs.http;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", path = "/usuarios")
public interface UsuariosClient {
    
    @GetMapping("/email/{email}")
    UsuariosDTO findByEmail(@PathVariable String email);
}
