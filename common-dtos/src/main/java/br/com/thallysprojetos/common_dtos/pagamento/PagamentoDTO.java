package br.com.thallysprojetos.common_dtos.pagamento;

import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.common_dtos.enums.TipoFormaPagamento;

public class PagamentoDTO {

    private Long id;
    private Double valor;
    private TipoFormaPagamento tipoPagamento;
    private StatusPagamento status;
    private String nomeTitularCartao;
    private String numeroCartao;
    private String expiracaoCartao;
    private String codigoCartao;
    private String codigoDeBarrasBoleto;
    private String chavePix;

    private Long pedidoId;

    public PagamentoDTO() {
    }

    public PagamentoDTO(Long id, Double valor, TipoFormaPagamento tipoPagamento, StatusPagamento status, String nomeTitularCartao, String numeroCartao, String expiracaoCartao, String codigoCartao, String codigoDeBarrasBoleto, String chavePix, Long pedidoId) {
        this.id = id;
        this.valor = valor;
        this.tipoPagamento = tipoPagamento;
        this.status = status;
        this.nomeTitularCartao = nomeTitularCartao;
        this.numeroCartao = numeroCartao;
        this.expiracaoCartao = expiracaoCartao;
        this.codigoCartao = codigoCartao;
        this.codigoDeBarrasBoleto = codigoDeBarrasBoleto;
        this.chavePix = chavePix;
        this.pedidoId = pedidoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public TipoFormaPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoFormaPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public String getNomeTitularCartao() {
        return nomeTitularCartao;
    }

    public void setNomeTitularCartao(String nomeTitularCartao) {
        this.nomeTitularCartao = nomeTitularCartao;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getExpiracaoCartao() {
        return expiracaoCartao;
    }

    public void setExpiracaoCartao(String expiracaoCartao) {
        this.expiracaoCartao = expiracaoCartao;
    }

    public String getCodigoCartao() {
        return codigoCartao;
    }

    public void setCodigoCartao(String codigoCartao) {
        this.codigoCartao = codigoCartao;
    }

    public String getCodigoDeBarrasBoleto() {
        return codigoDeBarrasBoleto;
    }

    public void setCodigoDeBarrasBoleto(String codigoDeBarrasBoleto) {
        this.codigoDeBarrasBoleto = codigoDeBarrasBoleto;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    @Override
    public String toString() {
        return "PagamentoDTO{" +
                "id=" + id +
                ", valor=" + valor +
                ", tipoPagamento=" + tipoPagamento +
                ", status=" + status +
                ", nomeTitularCartao='" + nomeTitularCartao + '\'' +
                ", numeroCartao='" + numeroCartao + '\'' +
                ", expiracaoCartao='" + expiracaoCartao + '\'' +
                ", codigoCartao='" + codigoCartao + '\'' +
                ", codigoDeBarrasBoleto='" + codigoDeBarrasBoleto + '\'' +
                ", chavePix='" + chavePix + '\'' +
                ", pedidoId=" + pedidoId +
                '}';
    }
}