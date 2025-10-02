package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        dto.setStatus(StatusPagamento.CRIADO);

        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.create", dto);
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
        PagamentoDTO pagamentoExistente = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (dto.getValor() != null) {
            pagamentoExistente.setValor(dto.getValor());
        }

        pagamentoExistente.setTipoPagamento(dto.getTipoPagamento());
        pagamentoExistente.setStatus(dto.getStatus());
        pagamentoExistente.setNomeTitularCartao(dto.getNomeTitularCartao());
        pagamentoExistente.setExpiracaoCartao(dto.getExpiracaoCartao());
        pagamentoExistente.setCodigoCartao(dto.getCodigoCartao());
        pagamentoExistente.setCodigoDeBarrasBoleto(dto.getCodigoDeBarrasBoleto());
        pagamentoExistente.setChavePix(dto.getChavePix());

        System.out.println("[USUARIOS] Enviando mensagem de atualização de usuário para RabbitMQ: " +
                "id=" + pagamentoExistente.getId() +
                "valor=" + pagamentoExistente.getValor() +
                ", tipoPagamento=" + pagamentoExistente.getTipoPagamento() +
                ", status=" + pagamentoExistente.getStatus() +
                ", nomeTitularCartao=" + pagamentoExistente.getNomeTitularCartao() +
                ", numeroCartao=" + pagamentoExistente.getNumeroCartao() +
                ", expiracaoCartao=" + pagamentoExistente.getExpiracaoCartao() +
                ", codigoCartao=" + pagamentoExistente.getCodigoCartao() +
                ", codigoDeBarrasBoleto=" + pagamentoExistente.getCodigoDeBarrasBoleto() +
                ", chavePix=" + pagamentoExistente.getChavePix());

        // ... Lógica para atualizar outros campos conforme necessário
        try {
            rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.update", pagamentoExistente);
            System.out.println("[PAGAMENTO] Enviando comando de atualização de pagamento enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PAGAMENTO] Erro ao enviar mensagem de atualização para RabbitMQ: " + e.getMessage());
            throw e;
        }
        return pagamentoExistente;
    }

//    public void deletePagamento(Long id) {
//        if (!databaseClient.existsById(id)) {
//            throw new PagamentoNotFoundException(String.format("Pagamento não encontrado com o id '%s'.", id));
//        }
//        databaseClient.deletePagamento(id);
//    }

    public void deletePagamento(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id);
        }

        System.out.println("[PAGAMENTO] Enviando comando de EXCLUSÃO de pagamento para RabbitMQ: PagamentoID=" + id);
        try {
            rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.delete", id);
            System.out.println("[PAGAMENTO] Mensagem de deleção enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PAGAMENTO] Erro ao enviar mensagem de deleção para RabbitMQ: " + e.getMessage());
            throw e;
        }

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
        PagamentoDTO pagamento = databaseClient.findById(id)
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado com o ID: " + id));

        if (pagamento.getStatus().equals(StatusPagamento.CRIADO) || pagamento.getStatus().equals(StatusPagamento.PROCESSADO)) {
            pagamento.setStatus(StatusPagamento.CONFIRMADO);
        } else {
            pagamento.setStatus(StatusPagamento.CANCELADO);
        }

        System.out.println("[PAGAMENTO] Enviando comando de PROCESSAMENTO de pagamento para RabbitMQ: PagamentoID=" + pagamento.getId() + ", Status=" + pagamento.getStatus());
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.update", pagamento);
    }

}