package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PagamentoAlreadyExistException extends ResponseStatusException {

    public PagamentoAlreadyExistException(){
        super(HttpStatus.CONFLICT, "Já existe um usuário com esse email");
    }

    public PagamentoAlreadyExistException(String message){
        super(HttpStatus.CONFLICT, message);
    }

}