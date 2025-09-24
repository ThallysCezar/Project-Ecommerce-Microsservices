package br.com.thallysprojetos.ms_produtos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_produtos")
@Getter
@Setter
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String tipoProduto;

    private String descricao;

    private Double preco;

    private boolean itemEstoque;

    private Integer estoque;

}