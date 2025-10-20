package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;

import java.util.List;


public interface PagamentoService {
    
    List<PagamentoDTO> findAll();

    PagamentoDTO findById(Long id);

    PagamentoDTO findByPedidoId(Long idPedido);

    PagamentoDTO createPayment(PagamentoDTO dto);

    PagamentoDTO updatePagamento(Long id, PagamentoDTO dto);

    void deletePagamento(Long id);

    void processarPagamento(Long id);

}