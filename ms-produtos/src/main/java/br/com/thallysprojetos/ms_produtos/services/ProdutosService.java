package br.com.thallysprojetos.ms_produtos.services;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosAlreadyExistException;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosNotFoundException;
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
public class ProdutosService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public List<ProdutosDTO> findAll() {
        return databaseClient.findAll();
    }

    public ProdutosDTO findProductById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, ProdutosDTO.class))
                .orElseThrow(ProdutosNotFoundException::new);
    }

    public ProdutosDTO createProduct(ProdutosDTO dto) {
        verificationExistingProduct(dto.getId(), dto);

        System.out.println(buildProdutoLogMessage("[PRODUTOS] Enviando mensagem de criação", null, dto));

        try {
            rabbitTemplate.convertAndSend("produtos.exchange", "produtos.create", dto);
            System.out.println("[PRODUTOS] Mensagem enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PRODUTOS] Erro ao enviar mensagem para RabbitMQ: " + e.getMessage());
            throw e;
        }
        return dto;
    }

    public List<ProdutosDTO> createProducts(List<ProdutosDTO> dtos) {
        for (ProdutosDTO dto : dtos) {
            verificationExistingProduct(dto.getId(), dto);
            
            System.out.println(buildProdutoLogMessage("[PRODUTOS] Enviando mensagem de criação", null, dto));
            try {
                rabbitTemplate.convertAndSend("produtos.exchange", "produtos.create", dto);
                System.out.println("[PRODUTOS] Mensagem enviada com sucesso!");
            } catch (Exception e) {
                System.out.println("[PRODUTOS] Erro ao enviar mensagem para RabbitMQ: " + e.getMessage());
                throw e;
            }
        }
        return dtos;
    }

    public ProdutosDTO updateProdutos(Long id, ProdutosDTO dto) {
        ProdutosDTO existingProduct = databaseClient.findById(id)
                .orElseThrow(() -> new ProdutosNotFoundException("Produto não encontrado com o ID: " + id));

        if (!existingProduct.getTitulo().equals(dto.getTitulo())) {
            verificationExistingProduct(id, dto);
        }

        System.out.println(buildProdutoLogMessage("[PRODUTOS] Enviando mensagem de atualização", id, dto));
        try {
            rabbitTemplate.convertAndSend("produtos.exchange", "produtos.update", dto);
            System.out.println("[PRODUTOS] Mensagem de atualização enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PRODUTOS] Erro ao enviar mensagem de atualização para RabbitMQ: " + e.getMessage());
            throw e;
        }
        return dto;
    }

    public void deleteProdutos(Long id) {
        System.out.println("[PRODUTOS] Enviando mensagem de deleção de produto para RabbitMQ. ID: " + id);
        try {
            rabbitTemplate.convertAndSend("produtos.exchange", "produtos.delete", id);
            System.out.println("[PRODUTOS] Mensagem de deleção enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[PRODUTOS] Erro ao enviar mensagem de deleção para RabbitMQ: " + e.getMessage());
            throw e;
        }
    }

    private String buildProdutoLogMessage(String prefixo, Long id, ProdutosDTO dto) {
        return "[PRODUTOS] " + prefixo + " de produto para RabbitMQ: " +
                "id=" + (id != null ? id : dto.getId()) +
                ", titulo=" + dto.getTitulo() +
                ", tipoProduto=" + dto.getTipoProduto() +
                ", descricao=" + dto.getDescricao() +
                ", preco=" + dto.getPreco() +
                ", itemEstoque=" + dto.isItemEstoque() +
                ", estoque=" + dto.getEstoque();
    }

    private void verificationExistingProduct(Long id, ProdutosDTO dto) {
        try {
            Optional<ProdutosDTO> productWithSameTitulo = databaseClient.findByTitulo(dto.getTitulo());
            if (productWithSameTitulo.isPresent() && !productWithSameTitulo.get().getId().equals(id)) {
                throw new ProdutosAlreadyExistException(
                        "Já existe um produto com o título: " + dto.getTitulo()
                );
            }
        } catch (FeignException.NotFound e) {
            // Título não encontrado no banco = título disponível para uso! ✅
            log.info("Título '{}' está disponível. Validação de duplicidade passou com sucesso.", dto.getTitulo());
        }
    }

}