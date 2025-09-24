package br.com.thallysprojetos.ms_produtos.exceptions.produtos;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProdutosException extends ResponseStatusException {

    public ProdutosException(){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Erro geral model");
    }

    public ProdutosException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
