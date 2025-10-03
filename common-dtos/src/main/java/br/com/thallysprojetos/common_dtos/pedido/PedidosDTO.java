
package br.com.thallysprojetos.common_dtos.pedido;

import br.com.thallysprojetos.common_dtos.enums.StatusPedidos;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PedidosDTO {

    private Long id;
    private LocalDateTime dataHora;
    private StatusPedidos statusPedidos;
    private List<ItemDoPedidoDTO> itens;
    private UsuarioIdDTO usuario;
    private PagamentoDTO pagamento;

    public PedidosDTO() {
    }

    public PedidosDTO(Long id, LocalDateTime dataHora, StatusPedidos statusPedidos, List<ItemDoPedidoDTO> itens, UsuarioIdDTO usuario, br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO pagamento) {
        this.id = id;
        this.dataHora = dataHora;
        this.statusPedidos = statusPedidos;
        this.itens = itens;
        this.usuario = usuario;
        this.pagamento = pagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusPedidos getStatusPedidos() {
        return statusPedidos;
    }

    public void setStatusPedidos(StatusPedidos statusPedidos) {
        this.statusPedidos = statusPedidos;
    }

    public List<ItemDoPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemDoPedidoDTO> itens) {
        this.itens = itens;
    }

    public UsuarioIdDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioIdDTO usuario) {
        this.usuario = usuario;
    }

    public PagamentoDTO getPagamento() {
        return pagamento;
    }

    public void setPagamento(PagamentoDTO pagamento) {
        this.pagamento = pagamento;
    }

}