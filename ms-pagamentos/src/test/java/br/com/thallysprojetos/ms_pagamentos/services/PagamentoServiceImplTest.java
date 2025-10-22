package br.com.thallysprojetos.ms_pagamentos.services;

import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.common_dtos.enums.TipoFormaPagamento;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoAlreadyExistException;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import br.com.thallysprojetos.ms_pagamentos.services.impl.PagamentoServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagamentoService Unit Tests")
public class PagamentoServiceImplTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PagamentoServiceImpl pagamentoService;

    private PagamentoDTO pagamentoDTO;

    @BeforeEach
    void setUp() {
        pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setId(1L);
        pagamentoDTO.setPedidoId(1L);
        pagamentoDTO.setValor(100.00);
        pagamentoDTO.setTipoPagamento(TipoFormaPagamento.CARTAO_CREDITO);
        pagamentoDTO.setStatus(StatusPagamento.CRIADO);
    }

    @Test
    @DisplayName("Should find all pagamentos successfully")
    void testFindAll_Success() {
        List<PagamentoDTO> expectedPagamentos = Arrays.asList(pagamentoDTO, new PagamentoDTO());
        when(databaseClient.findAll()).thenReturn(expectedPagamentos);

        List<PagamentoDTO> result = pagamentoService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(databaseClient, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find pagamento by ID successfully")
    void testFindById_Success() {
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        when(modelMapper.map(pagamentoDTO, PagamentoDTO.class)).thenReturn(pagamentoDTO);

        PagamentoDTO result = pagamentoService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(databaseClient, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw PagamentoNotFoundException when pagamento not found by ID")
    void testFindById_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.findById(999L))
                .isInstanceOf(PagamentoNotFoundException.class);

        verify(databaseClient, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find pagamento by pedido ID successfully")
    void testFindByPedidoId_Success() {
        when(databaseClient.findByPedidoId(1L)).thenReturn(Optional.of(pagamentoDTO));
        when(modelMapper.map(pagamentoDTO, PagamentoDTO.class)).thenReturn(pagamentoDTO);

        PagamentoDTO result = pagamentoService.findByPedidoId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPedidoId()).isEqualTo(1L);
        verify(databaseClient, times(1)).findByPedidoId(1L);
    }

    @Test
    @DisplayName("Should throw PagamentoNotFoundException when pagamento not found by pedido ID")
    void testFindByPedidoId_NotFound() {
        when(databaseClient.findByPedidoId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.findByPedidoId(999L))
                .isInstanceOf(PagamentoNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado para o pedido com ID");

        verify(databaseClient, times(1)).findByPedidoId(999L);
    }

    @Test
    @DisplayName("Should create payment successfully")
    void testCreatePayment_Success() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByPedidoId(1L)).thenThrow(notFoundException);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        PagamentoDTO result = pagamentoService.createPayment(pagamentoDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(StatusPagamento.CRIADO);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.create"),
                eq(pagamentoDTO)
        );
    }

    @Test
    @DisplayName("Should throw PagamentoAlreadyExistException when payment already exists for pedido")
    void testCreatePayment_PaymentAlreadyExists() {
        PagamentoDTO existingPayment = new PagamentoDTO();
        existingPayment.setId(2L);
        existingPayment.setPedidoId(1L);
        existingPayment.setStatus(StatusPagamento.CONFIRMADO);

        when(databaseClient.findByPedidoId(1L)).thenReturn(Optional.of(existingPayment));

        assertThatThrownBy(() -> pagamentoService.createPayment(pagamentoDTO))
                .isInstanceOf(PagamentoAlreadyExistException.class)
                .hasMessageContaining("Já existe um pagamento aprovado/processado");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should update pagamento successfully")
    void testUpdatePagamento_Success() {
        PagamentoDTO updatedDTO = new PagamentoDTO();
        updatedDTO.setValor(150.00);
        updatedDTO.setTipoPagamento(TipoFormaPagamento.PIX);
        updatedDTO.setStatus(StatusPagamento.PROCESSADO);

        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        PagamentoDTO result = pagamentoService.updatePagamento(1L, updatedDTO);

        assertThat(result).isNotNull();
        assertThat(result.getValor()).isEqualTo(150.00);
        assertThat(result.getTipoPagamento()).isEqualTo(TipoFormaPagamento.PIX);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.update"),
                any(PagamentoDTO.class)
        );
    }

    @Test
    @DisplayName("Should throw PagamentoNotFoundException when updating non-existent pagamento")
    void testUpdatePagamento_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.updatePagamento(999L, pagamentoDTO))
                .isInstanceOf(PagamentoNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should delete pagamento successfully")
    void testDeletePagamento_Success() {
        when(databaseClient.existsById(1L)).thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyLong());

        pagamentoService.deletePagamento(1L);

        verify(databaseClient, times(1)).existsById(1L);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.delete"),
                eq(1L)
        );
    }

    @Test
    @DisplayName("Should throw PagamentoNotFoundException when deleting non-existent pagamento")
    void testDeletePagamento_NotFound() {
        when(databaseClient.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> pagamentoService.deletePagamento(999L))
                .isInstanceOf(PagamentoNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should process payment successfully when status is CRIADO")
    void testProcessarPagamento_StatusCriado() {
        pagamentoDTO.setStatus(StatusPagamento.CRIADO);
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        pagamentoService.processarPagamento(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.update"),
                (Object) argThat(pagamento ->
                        pagamento instanceof PagamentoDTO &&
                                ((PagamentoDTO) pagamento).getStatus() == StatusPagamento.CONFIRMADO
                )
        );
    }

    @Test
    @DisplayName("Should process payment successfully when status is PROCESSADO")
    void testProcessarPagamento_StatusProcessado() {
        pagamentoDTO.setStatus(StatusPagamento.PROCESSADO);
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        pagamentoService.processarPagamento(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.update"),
                (Object) argThat(pagamento ->
                        pagamento instanceof PagamentoDTO &&
                                ((PagamentoDTO) pagamento).getStatus() == StatusPagamento.CONFIRMADO
                )
        );
    }

    @Test
    @DisplayName("Should cancel payment when status is not CRIADO or PROCESSADO")
    void testProcessarPagamento_StatusCancelado() {
        pagamentoDTO.setStatus(StatusPagamento.CANCELADO);
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        pagamentoService.processarPagamento(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.update"),
                (Object) argThat(pagamento ->
                        pagamento instanceof PagamentoDTO &&
                                ((PagamentoDTO) pagamento).getStatus() == StatusPagamento.CANCELADO
                )
        );
    }

    @Test
    @DisplayName("Should throw PagamentoNotFoundException when processing non-existent pagamento")
    void testProcessarPagamento_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.processarPagamento(999L))
                .isInstanceOf(PagamentoNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should handle RabbitMQ exception during payment update")
    void testUpdatePagamento_RabbitMQException() {
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pagamentoDTO));
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        assertThatThrownBy(() -> pagamentoService.updatePagamento(1L, pagamentoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RabbitMQ connection failed");
    }

}