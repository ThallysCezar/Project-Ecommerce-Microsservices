package br.com.thallysprojetos.ms_pedidos.service;

import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.common_dtos.enums.StatusPedidos;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.common_dtos.pedido.UsuarioIdDTO;
import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_pedidos.configs.https.DatabaseClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.ProdutosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.UsuariosClient;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosAlreadyExistException;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosNotFoundException;
import br.com.thallysprojetos.ms_pedidos.service.impl.PedidosServiceImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidosService Unit Tests")
public class PedidosServiceImplTest {

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private ProdutosClient produtosClient;

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidosServiceImpl pedidosService;

    private PedidosDTO pedidoDTO;
    private UsuariosDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        UsuarioIdDTO usuarioIdDTO = new UsuarioIdDTO();
        usuarioIdDTO.setId(1L);

        usuarioDTO = new UsuariosDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setEmail("test@example.com");

        ProdutosDTO produtoDTO = new ProdutosDTO();
        produtoDTO.setId(1L);
        produtoDTO.setTitulo("Produto Teste");

        pedidoDTO = new PedidosDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setUsuario(usuarioIdDTO);
        pedidoDTO.setStatusPedidos(StatusPedidos.CRIADO);
    }

    @Test
    @DisplayName("Should find all pedidos successfully")
    void testFindAll_Success() {
        List<PedidosDTO> expectedPedidos = Arrays.asList(pedidoDTO, new PedidosDTO());
        when(databaseClient.findAll()).thenReturn(expectedPedidos);

        List<PedidosDTO> result = pedidosService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(databaseClient, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find pedido by ID successfully")
    void testFindById_Success() {
        when(databaseClient.findById(1L)).thenReturn(Optional.of(pedidoDTO));
        when(modelMapper.map(pedidoDTO, PedidosDTO.class)).thenReturn(pedidoDTO);

        PedidosDTO result = pedidosService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(databaseClient, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw PedidosNotFoundException when pedido not found by ID")
    void testFindById_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidosService.findById(999L))
                .isInstanceOf(PedidosNotFoundException.class);

        verify(databaseClient, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find pedidos by user ID successfully")
    void testFindByUserId_Success() {
        List<PedidosDTO> pedidos = Arrays.asList(pedidoDTO);
        when(databaseClient.findByUsuarioId(1L)).thenReturn(pedidos);
        when(modelMapper.map(any(PedidosDTO.class), eq(PedidosDTO.class))).thenReturn(pedidoDTO);

        List<PedidosDTO> result = pedidosService.findByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(databaseClient, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Should throw PedidosNotFoundException when no pedidos found for user")
    void testFindByUserId_NotFound() {
        when(databaseClient.findByUsuarioId(999L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> pedidosService.findByUserId(999L))
                .isInstanceOf(PedidosNotFoundException.class)
                .hasMessageContaining("Nenhum pedido encontrado");

        verify(databaseClient, times(1)).findByUsuarioId(999L);
    }

    @Test
    @DisplayName("Should create pedido successfully")
    void testCreatePedido_Success() {
        when(usuariosClient.findById(1L)).thenReturn(usuarioDTO);
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findPendingByUsuarioId(1L)).thenThrow(notFoundException);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PedidosDTO.class));
        
        PedidosDTO result = pedidosService.createPedido(pedidoDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStatusPedidos()).isEqualTo(StatusPedidos.CRIADO);
        assertThat(result.getDataHora()).isNotNull();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pedidos.create"),
                any(PedidosDTO.class)
        );
    }

    @Test
    @DisplayName("Should throw RuntimeException when usuario is null")
    void testCreatePedido_UsuarioNull() {
        pedidoDTO.setUsuario(null);

        assertThatThrownBy(() -> pedidosService.createPedido(pedidoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não fornecido");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should throw PedidosAlreadyExistException when user has pending orders")
    void testCreatePedido_PendingOrdersExist() {
        List<PedidosDTO> pendingOrders = Arrays.asList(new PedidosDTO(), new PedidosDTO());

        when(usuariosClient.findById(1L)).thenReturn(usuarioDTO);
        when(databaseClient.findPendingByUsuarioId(1L)).thenReturn(pendingOrders);

        assertThatThrownBy(() -> pedidosService.createPedido(pedidoDTO))
                .isInstanceOf(PedidosAlreadyExistException.class)
                .hasMessageContaining("pedido(s) pendente(s)");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should process payment for pedido successfully")
    void testProcessarPagamentoDoPedido_Success() {
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setValor(100.00);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PagamentoDTO.class));

        pedidosService.processarPagamentoDoPedido(1L, pagamentoDTO);
        assertThat(pagamentoDTO.getPedidoId()).isEqualTo(1L);
        assertThat(pagamentoDTO.getStatus()).isEqualTo(StatusPagamento.PROCESSADO);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pagamentos.exchange"),
                eq("pagamentos.create"),
                eq(pagamentoDTO)
        );
    }

    @Test
    @DisplayName("Should throw RuntimeException when pagamento is null")
    void testProcessarPagamentoDoPedido_PagamentoNull() {
        assertThatThrownBy(() -> pedidosService.processarPagamentoDoPedido(1L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Dados do pagamento não fornecidos");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should update pedido successfully")
    void testUpdatePedidos_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PedidosDTO.class));

        pedidosService.updatePedidos(1L, pedidoDTO);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pedidos.update"),
                eq(pedidoDTO)
        );
    }

    @Test
    @DisplayName("Should delete pedido successfully")
    void testDeletePedidos_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        pedidosService.deletePedidos(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pedidos.delete"),
                (Object) eq(1L)
        );
    }

    @Test
    @DisplayName("Should confirm pedido successfully")
    void testConfirmarPedidos_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        pedidosService.confirmarPedidos(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pedidos.confirm"),
                (Object) eq(1L)
        );
    }

    @Test
    @DisplayName("Should cancel pedido successfully")
    void testCancelarPedido_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        pedidosService.cancelarPedido(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pedidos.cancel"),
                (Object) eq(1L)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ exception during pedido update")
    void testUpdatePedidos_RabbitMQException() {
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PedidosDTO.class));

        assertThatThrownBy(() -> pedidosService.updatePedidos(1L, pedidoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RabbitMQ connection failed");
    }

}