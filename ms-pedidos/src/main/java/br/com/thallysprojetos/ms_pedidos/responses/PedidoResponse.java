package br.com.thallysprojetos.ms_pedidos.responses;

import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class PedidoResponse extends RepresentationModel<PedidoResponse> {
    
    private final PedidosDTO pedido;
    private final String mensagem;

    public PedidoResponse(PedidosDTO pedido, String mensagem) {
        this.pedido = pedido;
        this.mensagem = mensagem;
    }
}
