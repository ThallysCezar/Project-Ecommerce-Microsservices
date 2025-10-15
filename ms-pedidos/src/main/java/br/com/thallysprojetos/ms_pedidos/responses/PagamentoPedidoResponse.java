package br.com.thallysprojetos.ms_pedidos.responses;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class PagamentoPedidoResponse extends RepresentationModel<PagamentoPedidoResponse> {
    
    private final Long pedidoId;
    private final String mensagem;

    public PagamentoPedidoResponse(Long pedidoId, String mensagem) {
        this.pedidoId = pedidoId;
        this.mensagem = mensagem;
    }
}
