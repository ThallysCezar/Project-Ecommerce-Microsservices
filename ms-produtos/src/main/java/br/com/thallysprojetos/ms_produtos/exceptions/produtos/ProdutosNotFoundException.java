package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProdutosNotFoundException extends ResponseStatusException {

    public ProdutosNotFoundException(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Produtos não encontrado com esse id");
    }

    public ProdutosNotFoundException(String message){
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}