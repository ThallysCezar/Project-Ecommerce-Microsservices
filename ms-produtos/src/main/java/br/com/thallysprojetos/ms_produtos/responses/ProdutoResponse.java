package br.com.thallysprojetos.ms_produtos.responses;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
public class ProdutoResponse extends RepresentationModel<ProdutoResponse> {
    
    private final Object produtos; // Pode ser um único ProdutosDTO ou List<ProdutosDTO>
    private final String mensagem;

    public ProdutoResponse(ProdutosDTO produto, String mensagem) {
        this.produtos = produto;
        this.mensagem = mensagem;
    }

    public ProdutoResponse(List<ProdutosDTO> produtos, String mensagem) {
        this.produtos = produtos;
        this.mensagem = mensagem;
    }

}