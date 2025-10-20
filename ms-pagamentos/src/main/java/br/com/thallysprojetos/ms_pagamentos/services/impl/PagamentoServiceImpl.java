package br.com.thallysprojetos.ms_pagamentos.services.impl;

import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoAlreadyExistException;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import br.com.thallysprojetos.ms_pagamentos.services.PagamentoService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PagamentoServiceImpl implements PagamentoService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    @Override
    public List<PagamentoDTO> findAll() {
        return databaseClient.findAll();
    }

    @Override
    public PagamentoDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(PagamentoNotFoundException::new);
    }

    @Override
    public PagamentoDTO findByPedidoId(Long idPedido) {
        return databaseClient.findByPedidoId(idPedido)
                .map(p -> modelMapper.map(p, PagamentoDTO.class))
                .orElseThrow(() -> new PagamentoNotFoundException("Pagamento não encontrado para o pedido com ID: " + idPedido));
    }

    @Override
    public PagamentoDTO createPayment(PagamentoDTO dto) {
        verificationExistingPaymentForPedido(dto.getPedidoId());
        
        dto.setStatus(StatusPagamento.CRIADO);

        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.create", dto);
        return dto;
    }

    @Override
    public PagamentoDTO updatePagamento(Long id, PagamentoDTO dto) {
        PagamentoDTO pagamentoExistente = getPagamentoExistente(id, dto);
        try {
            rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.update", pagamentoExistente);
            System.out.println("[PAGAMENTO] Enviando comando de atualização de pagamento enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PAGAMENTO] Erro ao enviar mensagem de atualização para RabbitMQ: " + e.getMessage());
            throw e;
        }
        return pagamentoExistente;
    }

    @Override
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

    @Override
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

    private PagamentoDTO getPagamentoExistente(Long id, PagamentoDTO dto) {
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
        return pagamentoExistente;
    }

    private void verificationExistingPaymentForPedido(Long pedidoId) {
        try {
            Optional<PagamentoDTO> existingPayment = databaseClient.findByPedidoId(pedidoId);
            if (existingPayment.isPresent() && 
                (existingPayment.get().getStatus() == StatusPagamento.CONFIRMADO || 
                 existingPayment.get().getStatus() == StatusPagamento.PROCESSADO)) {
                throw new PagamentoAlreadyExistException(
                        "Já existe um pagamento aprovado/processado para o pedido ID: " + pedidoId
                );
            }
        } catch (FeignException.NotFound e) {
            // Pedido sem pagamento = pode criar pagamento! ✅
            log.info("Nenhum pagamento encontrado para o pedido ID {}. Validação passou com sucesso.", pedidoId);
        }
    }

}
