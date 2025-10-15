package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

public class ProdutosNotFoundException extends RuntimeException {

    public ProdutosNotFoundException(){
        super("Produtos não encontrado com esse id");
    }

    public ProdutosNotFoundException(String message){
        super(message);
    }

}