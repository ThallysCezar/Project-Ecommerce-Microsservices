package br.com.thallysprojetos.ms_produtos.services;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosAlreadyExistException;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosNotFoundException;
import br.com.thallysprojetos.ms_produtos.services.impl.ProdutosServiceImpl;
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
@DisplayName("ProdutosService Unit Tests")
public class ProdutosServiceImplTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProdutosServiceImpl produtosService;

    private ProdutosDTO produtoDTO;

    @BeforeEach
    void setUp() {
        produtoDTO = new ProdutosDTO();
        produtoDTO.setId(1L);
        produtoDTO.setTitulo("Notebook Dell");
        produtoDTO.setDescricao("Notebook Dell Inspiron 15");
        produtoDTO.setTipoProduto("ELETRONICO");
        produtoDTO.setPreco(3500.00);
        produtoDTO.setItemEstoque(true);
        produtoDTO.setEstoque(10);
    }

    @Test
    @DisplayName("Should find all products successfully")
    void testFindAll_Success() {
        List<ProdutosDTO> expectedProducts = Arrays.asList(produtoDTO, new ProdutosDTO());

        when(databaseClient.findAll()).thenReturn(expectedProducts);
        List<ProdutosDTO> result = produtosService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(databaseClient, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void testFindProductById_Success() {
        when(databaseClient.findById(1L)).thenReturn(Optional.of(produtoDTO));
        when(modelMapper.map(produtoDTO, ProdutosDTO.class)).thenReturn(produtoDTO);

        ProdutosDTO result = produtosService.findProductById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitulo()).isEqualTo("Notebook Dell");

        verify(databaseClient, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProdutosNotFoundException when product not found by ID")
    void testFindProductById_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> produtosService.findProductById(999L))
                .isInstanceOf(ProdutosNotFoundException.class);

        verify(databaseClient, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct_Success() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByTitulo(anyString())).thenThrow(notFoundException);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ProdutosDTO.class));

        ProdutosDTO result = produtosService.createProduct(produtoDTO);
        assertThat(result).isNotNull();
        assertThat(result.getTitulo()).isEqualTo("Notebook Dell");

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("produtos.exchange"),
                eq("produtos.create"),
                eq(produtoDTO)
        );
    }

    @Test
    @DisplayName("Should throw ProdutosAlreadyExistException when titulo already exists")
    void testCreateProduct_TituloAlreadyExists() {
        ProdutosDTO existingProduct = new ProdutosDTO();
        existingProduct.setId(2L);
        existingProduct.setTitulo("Notebook Dell");

        when(databaseClient.findByTitulo("Notebook Dell")).thenReturn(Optional.of(existingProduct));

        assertThatThrownBy(() -> produtosService.createProduct(produtoDTO))
                .isInstanceOf(ProdutosAlreadyExistException.class)
                .hasMessageContaining("Já existe um produto com o título");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should create multiple products successfully")
    void testCreateProducts_Success() {
        ProdutosDTO produto2 = new ProdutosDTO();
        produto2.setId(2L);
        produto2.setTitulo("Mouse Logitech");

        List<ProdutosDTO> produtos = Arrays.asList(produtoDTO, produto2);

        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);

        when(databaseClient.findByTitulo(anyString())).thenThrow(notFoundException);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ProdutosDTO.class));

        List<ProdutosDTO> result = produtosService.createProducts(produtos);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(rabbitTemplate, times(2)).convertAndSend(
                eq("produtos.exchange"),
                eq("produtos.create"),
                any(ProdutosDTO.class)
        );
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProdutos_Success() {
        ProdutosDTO updatedDTO = new ProdutosDTO();
        updatedDTO.setTitulo("Notebook Dell");
        updatedDTO.setDescricao("Updated Description");
        updatedDTO.setPreco(4000.00);

        when(databaseClient.findById(1L)).thenReturn(Optional.of(produtoDTO));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ProdutosDTO.class));
        ProdutosDTO result = produtosService.updateProdutos(1L, updatedDTO);

        assertThat(result).isNotNull();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("produtos.exchange"),
                eq("produtos.update"),
                eq(updatedDTO)
        );
    }

    @Test
    @DisplayName("Should throw ProdutosNotFoundException when updating non-existent product")
    void testUpdateProdutos_NotFound() {
        when(databaseClient.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> produtosService.updateProdutos(999L, produtoDTO))
                .isInstanceOf(ProdutosNotFoundException.class)
                .hasMessageContaining("Produto não encontrado com o ID");

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should throw ProdutosAlreadyExistException when updating to existing titulo")
    void testUpdateProdutos_TituloAlreadyExists() {
        ProdutosDTO updatedDTO = new ProdutosDTO();
        updatedDTO.setTitulo("Mouse Logitech");

        ProdutosDTO existingWithTitulo = new ProdutosDTO();
        existingWithTitulo.setId(2L);
        existingWithTitulo.setTitulo("Mouse Logitech");

        when(databaseClient.findById(1L)).thenReturn(Optional.of(produtoDTO));
        when(databaseClient.findByTitulo("Mouse Logitech")).thenReturn(Optional.of(existingWithTitulo));
        assertThatThrownBy(() -> produtosService.updateProdutos(1L, updatedDTO))
                .isInstanceOf(ProdutosAlreadyExistException.class);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProdutos_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyLong());
        produtosService.deleteProdutos(1L);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("produtos.exchange"),
                eq("produtos.delete"),
                eq(1L)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ exception during product creation")
    void testCreateProduct_RabbitMQException() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(databaseClient.findByTitulo(anyString())).thenThrow(notFoundException);

        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ProdutosDTO.class));

        assertThatThrownBy(() -> produtosService.createProduct(produtoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RabbitMQ connection failed");
    }

}