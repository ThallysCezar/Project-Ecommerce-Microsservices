package br.com.thallysprojetos.ms_database.dtos.produtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProdutosDTO {

    private Long id;
    private String titulo;
    private String tipoProduto;
    private String descricao;
    private Double preco;
    private boolean itemEstoque;
    private Integer estoque;

}