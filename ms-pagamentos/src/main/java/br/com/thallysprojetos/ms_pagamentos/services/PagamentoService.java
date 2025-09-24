package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.ms_pagamentos.dtos.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import br.com.thallysprojetos.ms_pagamentos.models.Pagamento;
import br.com.thallysprojetos.ms_pagamentos.models.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.repositories.PagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ModelMapper modelMapper;

    public Page<PagamentoDTO> findAll(Pageable page) {
        return pagamentoRepository.findAll(page).map(p -> modelMapper.map(p, PagamentoDTO.class));
    }

    public PagamentoDTO findById(Long id) {
        return pagamentoRepository.findById(id)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(PagamentoNotFoundException::new);
    }

    public PagamentoDTO findByPedidoId(Long idPedido) {
        return pagamentoRepository.findByPedidoId(idPedido)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado para o pedido com ID: " + idPedido));
    }

    // NOVO MÉTODO PARA CRIAR PAGAMENTO (será chamado pelo ms-pedidos)
    @Transactional
    public PagamentoDTO createPayment(PagamentoDTO dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(StatusPagamento.CRIADO);
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamentoSalvo, PagamentoDTO.class);
    }

    @Transactional
    public PagamentoDTO updatePagamento(Long id, PagamentoDTO dto) {
        Pagamento pagamentoExistente = pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (dto.getValor() != null) {
            pagamentoExistente.setValor(dto.getValor());
        }

        Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamentoExistente);
        return modelMapper.map(pagamentoAtualizado, PagamentoDTO.class);
    }

    @Transactional
    public void deletePagamento(Long id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new PagamentoNotFoundException(String.format("Pagamento não encontrado com o id '%s'.", id));
        }
        pagamentoRepository.deleteById(id);
    }

    public void processarPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (pagamento.getStatus().equals(StatusPagamento.CRIADO)) {
            pagamento.setStatus(StatusPagamento.CONFIRMADO);
        } else {
            pagamento.setStatus(StatusPagamento.CANCELADO);
        }

        pagamentoRepository.save(pagamento);
    }

}