package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

public class ProdutosException extends RuntimeException {

    public ProdutosException(){
        super("Erro ao processar produto");
    }

    public ProdutosException(String message){
        super(message);
    }

}
