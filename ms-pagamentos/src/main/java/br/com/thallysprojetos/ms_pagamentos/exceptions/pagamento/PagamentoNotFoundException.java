package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PagamentoNotFoundException extends ResponseStatusException {

    public PagamentoNotFoundException(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Usuario não encontrado com esse id");
    }

    public PagamentoNotFoundException(String message){
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}