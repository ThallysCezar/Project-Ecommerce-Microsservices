package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PagamentoException extends ResponseStatusException {

    public PagamentoException(){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Erro geral model");
    }

    public PagamentoException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
