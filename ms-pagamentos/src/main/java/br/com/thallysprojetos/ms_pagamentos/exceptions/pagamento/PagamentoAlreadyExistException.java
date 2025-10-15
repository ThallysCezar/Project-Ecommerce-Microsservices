package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

public class PagamentoAlreadyExistException extends RuntimeException {

    public PagamentoAlreadyExistException(){
        super("Já existe um pagamento com esse identificador");
    }

    public PagamentoAlreadyExistException(String message){
        super(message);
    }

}