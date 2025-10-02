package br.com.thallysprojetos.common_dtos.pedido;

public class PagamentoPedidoUpdateDTO {
    private Long pedidoId;
    private Long pagamentoId;

    public PagamentoPedidoUpdateDTO() {
    }

    public PagamentoPedidoUpdateDTO(Long pedidoId, Long pagamentoId) {
        this.pedidoId = pedidoId;
        this.pagamentoId = pagamentoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }
}
