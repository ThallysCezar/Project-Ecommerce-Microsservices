package br.com.thallysprojetos.ms_pagamentos.responses;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class ConfirmacaoPagamentoResponse extends RepresentationModel<ConfirmacaoPagamentoResponse> {
    
    private final Long pagamentoId;
    private final Long pedidoId;
    private final String mensagem;
    private final String status;

    public ConfirmacaoPagamentoResponse(Long pagamentoId, Long pedidoId, String mensagem, String status) {
        this.pagamentoId = pagamentoId;
        this.pedidoId = pedidoId;
        this.mensagem = mensagem;
        this.status = status;
    }
}
