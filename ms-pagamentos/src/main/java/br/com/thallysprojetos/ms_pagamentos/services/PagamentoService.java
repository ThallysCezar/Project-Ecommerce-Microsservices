package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.ms_pagamentos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_pagamentos.dtos.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.dtos.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public List<PagamentoDTO> findAll() {
        return databaseClient.findAll();
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

//    public PagamentoDTO createPayment(PagamentoDTO dto) {
//        dto.setStatus(StatusPagamento.CRIADO);
//        return databaseClient.createPagamento(dto);
//    }

    public PagamentoDTO createPayment(PagamentoDTO dto) {
        // Define o status inicial no serviço de negócio
        dto.setStatus(StatusPagamento.CRIADO);

        // Envia o comando de criação para o ms-database (assíncrono)
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.create", dto);

        // Retorna o DTO de entrada (o ID real será gerado de forma assíncrona)
        return dto;
    }

//    public PagamentoDTO updatePagamento(Long id, PagamentoDTO dto) {
//        PagamentoDTO pagamentoExistente = databaseClient.findById(id)
//                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));
//
//        if (dto.getValor() != null) {
//            pagamentoExistente.setValor(dto.getValor());
//        }
//
//        return databaseClient.updatePagamento(id, pagamentoExistente);
//    }

    public PagamentoDTO updatePagamento(Long id, PagamentoDTO dto) {
        // 1. Busca o estado atual do pagamento (síncrono)
        PagamentoDTO pagamentoExistente = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        // 2. Aplica as alterações no DTO existente
        if (dto.getValor() != null) {
            pagamentoExistente.setValor(dto.getValor());
        }
        // ... Lógica para atualizar outros campos conforme necessário

        // 3. Envia o comando de atualização para o ms-database (assíncrono)
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.update", pagamentoExistente);

        // Retorna o DTO atualizado
        return pagamentoExistente;
    }

//    public void deletePagamento(Long id) {
//        if (!databaseClient.existsById(id)) {
//            throw new PagamentoNotFoundException(String.format("Pagamento não encontrado com o id '%s'.", id));
//        }
//        databaseClient.deletePagamento(id);
//    }

    public void deletePagamento(Long id) {
        // A checagem de existência pode ser feita antes, mas para simplicidade,
        // vamos enviar o comando e deixar o Listener tratar a exceção.

        // Envia o comando de exclusão (assíncrono)
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.delete", id);
    }

//    public void processarPagamento(Long id) {
//        PagamentoDTO pagamento = databaseClient.findById(id)
//                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));
//
//        if (pagamento.getStatus().equals(StatusPagamento.CRIADO)) {
//            pagamento.setStatus(StatusPagamento.CONFIRMADO);
//        } else {
//            pagamento.setStatus(StatusPagamento.CANCELADO);
//        }
//
//        databaseClient.updatePagamento(id, pagamento);
//    }

    public void processarPagamento(Long id) {
        // 1. Busca o estado atual do pagamento (síncrono)
        PagamentoDTO pagamento = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        // 2. Aplica a lógica de transição de status
        if (pagamento.getStatus().equals(StatusPagamento.CRIADO)) {
            pagamento.setStatus(StatusPagamento.CONFIRMADO);
        } else {
            pagamento.setStatus(StatusPagamento.CANCELADO);
        }

        // 3. Envia o DTO atualizado como um comando de atualização (assíncrono)
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.update", pagamento);
    }

}