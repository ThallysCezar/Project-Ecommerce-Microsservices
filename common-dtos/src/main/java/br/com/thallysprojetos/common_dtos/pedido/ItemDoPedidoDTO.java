package br.com.thallysprojetos.common_dtos.pedido;

public class ItemDoPedidoDTO {

    private Long id;
    private Integer quantidade;
    private String descricao;
    private ProdutoIdDTO produto;

    public ItemDoPedidoDTO() {
    }

    public ItemDoPedidoDTO(Long id, Integer quantidade, String descricao, ProdutoIdDTO produto) {
        this.id = id;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ProdutoIdDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoIdDTO produto) {
        this.produto = produto;
    }

}