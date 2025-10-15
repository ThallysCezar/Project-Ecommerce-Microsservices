package br.com.thallysprojetos.ms_pedidos.responses;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class ConfirmacaoPedidoResponse extends RepresentationModel<ConfirmacaoPedidoResponse> {
    
    private final Long pedidoId;
    private final String mensagem;
    private final String status;

    public ConfirmacaoPedidoResponse(Long pedidoId, String mensagem, String status) {
        this.pedidoId = pedidoId;
        this.mensagem = mensagem;
        this.status = status;
    }
}
