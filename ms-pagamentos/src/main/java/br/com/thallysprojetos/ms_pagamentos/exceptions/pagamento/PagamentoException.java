package br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento;

public class PagamentoException extends RuntimeException {

    public PagamentoException(){
        super("Erro ao processar pagamento");
    }

    public PagamentoException(String message){
        super(message);
    }

}
