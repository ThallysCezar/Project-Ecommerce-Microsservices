package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

public class ProdutosAlreadyExistException extends RuntimeException {

    public ProdutosAlreadyExistException(){
        super("Já existe esse produto");
    }

    public ProdutosAlreadyExistException(String message){
        super(message);
    }

}