package br.com.thallysprojetos.common_dtos.produto;

public class ProdutosDTO {

    private Long id;
    private String titulo;
    private String tipoProduto;
    private String descricao;
    private Double preco;
    private boolean itemEstoque;
    private Integer estoque;

    public ProdutosDTO() {
    }

    public ProdutosDTO(Long id, String titulo, String tipoProduto, String descricao, Double preco, boolean itemEstoque, Integer estoque) {
        this.id = id;
        this.titulo = titulo;
        this.tipoProduto = tipoProduto;
        this.descricao = descricao;
        this.preco = preco;
        this.itemEstoque = itemEstoque;
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public boolean isItemEstoque() {
        return itemEstoque;
    }

    public void setItemEstoque(boolean itemEstoque) {
        this.itemEstoque = itemEstoque;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return "ProdutosDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tipoProduto='" + tipoProduto + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", itemEstoque=" + itemEstoque +
                ", estoque=" + estoque +
                '}';
    }

}