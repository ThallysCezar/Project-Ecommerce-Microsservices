package br.com.thallysprojetos.ms_produtos.services;

import br.com.thallysprojetos.ms_produtos.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_produtos.dtos.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProdutosService {

    private final DatabaseClient databaseClient;
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
        return databaseClient.createProduto(dto);
    }

    public List<ProdutosDTO> createProducts(List<ProdutosDTO> dtos) {
        return dtos.stream()
                .map(databaseClient::createProduto)
                .collect(Collectors.toList());
    }

    public ProdutosDTO updateProdutos(Long id, ProdutosDTO dto) {
        ProdutosDTO produtoExistente = databaseClient.findById(id)
                .orElseThrow(() -> new ProdutosNotFoundException("Produto não encontrado com o ID: " + id));

        produtoExistente.setTitulo(dto.getTitulo());
        produtoExistente.setTipoProduto(dto.getTipoProduto());
        produtoExistente.setDescricao(dto.getDescricao());
        produtoExistente.setPreco(dto.getPreco());
        produtoExistente.setItemEstoque(dto.isItemEstoque());
        produtoExistente.setEstoque(dto.getEstoque());

        return databaseClient.updateProduto(id, produtoExistente);
    }

    public void deleteProdutos(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new ProdutosNotFoundException(String.format("Produtos não encontrado com o id '%s'.", id));
        }
        databaseClient.deleteProduto(id);
    }

}