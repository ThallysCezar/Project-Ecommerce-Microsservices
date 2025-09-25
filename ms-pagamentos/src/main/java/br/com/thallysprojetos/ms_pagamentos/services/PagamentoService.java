package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.ms_pagamentos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_pagamentos.dtos.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.dtos.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final DatabaseClient databaseClient;
    private final ModelMapper modelMapper;

    public Page<PagamentoDTO> findAll(Pageable page) {
        return databaseClient.findAll(page).map(p -> modelMapper.map(p, PagamentoDTO.class));
    }

    public PagamentoDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(PagamentoNotFoundException::new);
    }

    public PagamentoDTO findByPedidoId(Long idPedido) {
        return databaseClient.findByPedidoId(idPedido)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado para o pedido com ID: " + idPedido));
    }

    public PagamentoDTO createPayment(PagamentoDTO dto) {
        dto.setStatus(StatusPagamento.CRIADO);
        return databaseClient.createPagamento(dto);
    }

    public PagamentoDTO updatePagamento(Long id, PagamentoDTO dto) {
        PagamentoDTO pagamentoExistente = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (dto.getValor() != null) {
            pagamentoExistente.setValor(dto.getValor());
        }

        return databaseClient.updatePagamento(id, pagamentoExistente);
    }

    public void deletePagamento(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new PagamentoNotFoundException(String.format("Pagamento não encontrado com o id '%s'.", id));
        }
        databaseClient.deletePagamento(id);
    }

    public void processarPagamento(Long id) {
        PagamentoDTO pagamento = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (pagamento.getStatus().equals(StatusPagamento.CRIADO)) {
            pagamento.setStatus(StatusPagamento.CONFIRMADO);
        } else {
            pagamento.setStatus(StatusPagamento.CANCELADO);
        }

        databaseClient.updatePagamento(id, pagamento);
    }

}