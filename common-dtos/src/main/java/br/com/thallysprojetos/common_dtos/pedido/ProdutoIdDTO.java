package br.com.thallysprojetos.common_dtos.pedido;

public class ProdutoIdDTO {

    private Long id;

    public ProdutoIdDTO() {
    }

    public ProdutoIdDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}