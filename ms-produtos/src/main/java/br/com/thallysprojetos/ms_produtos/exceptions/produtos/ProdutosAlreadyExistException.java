package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProdutosAlreadyExistException extends ResponseStatusException {

    public ProdutosAlreadyExistException(){
        super(HttpStatus.CONFLICT, "Já existe esse produto");
    }

    public ProdutosAlreadyExistException(String message){
        super(HttpStatus.CONFLICT, message);
    }

}