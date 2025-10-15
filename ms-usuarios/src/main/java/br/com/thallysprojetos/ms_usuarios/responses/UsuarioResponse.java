package br.com.thallysprojetos.ms_usuarios.responses;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class UsuarioResponse extends RepresentationModel<UsuarioResponse> {
    
    private final UsuariosDTO usuario;
    private final String mensagem;

    public UsuarioResponse(UsuariosDTO usuario, String mensagem) {
        this.usuario = usuario;
        this.mensagem = mensagem;
    }
}
