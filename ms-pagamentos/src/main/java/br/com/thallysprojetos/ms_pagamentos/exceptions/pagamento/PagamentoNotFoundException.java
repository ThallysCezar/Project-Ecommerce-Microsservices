package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

public class PagamentoNotFoundException extends RuntimeException {

    public PagamentoNotFoundException(){
        super("Pagamento não encontrado");
    }

    public PagamentoNotFoundException(String message){
        super(message);
    }

}