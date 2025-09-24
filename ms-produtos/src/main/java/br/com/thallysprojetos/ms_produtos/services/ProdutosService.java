package br.com.thallysprojetos.ms_produtos.services;

import br.com.thallysprojetos.ms_produtos.dtos.ProdutosDTO;
import br.com.thallysprojetos.ms_produtos.exceptions.produtos.ProdutosNotFoundException;
import br.com.thallysprojetos.ms_produtos.models.Produtos;
import br.com.thallysprojetos.ms_produtos.repositories.ProdutosRepository;
import jakarta.transaction.Transactional;
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

    private final ProdutosRepository produtosRepository;
    private final ModelMapper modelMapper;

    public Page<ProdutosDTO> findAll(Pageable page) {
        return produtosRepository.findAll(page).map(p -> modelMapper.map(p, ProdutosDTO.class));
    }

    public ProdutosDTO findProductById(Long id) {
        return produtosRepository.findById(id)
                .map(p -> modelMapper.map(p, ProdutosDTO.class))
                .orElseThrow(ProdutosNotFoundException::new);
    }

    @Transactional
    public ProdutosDTO createProduct(ProdutosDTO dto) {
        Produtos produtos = modelMapper.map(dto, Produtos.class);
        Produtos produtosSaved = produtosRepository.save(produtos);
        return modelMapper.map(produtosSaved, ProdutosDTO.class);
    }

    @Transactional
    public List<ProdutosDTO> createProducts(List<ProdutosDTO> dtos) {
        return dtos.stream()
                .map(dto -> {
                    Produtos produto = modelMapper.map(dto, Produtos.class);
                    Produtos produtoSalvo = produtosRepository.save(produto);
                    return modelMapper.map(produtoSalvo, ProdutosDTO.class);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutosDTO updateProdutos(Long id, ProdutosDTO dto) {
        Produtos produtoExistente = produtosRepository.findById(id)
                .orElseThrow(() -> new ProdutosNotFoundException("Produto não encontrado com o ID: " + id));

        produtoExistente.setTitulo(dto.getTitulo());
        produtoExistente.setTipoProduto(dto.getTipoProduto());
        produtoExistente.setDescricao(dto.getDescricao());
        produtoExistente.setPreco(dto.getPreco());
        produtoExistente.setItemEstoque(dto.isItemEstoque());
        produtoExistente.setEstoque(dto.getEstoque());

        Produtos produtoAtualizado = produtosRepository.save(produtoExistente);
        return modelMapper.map(produtoAtualizado, ProdutosDTO.class);
    }

    @Transactional
    public void deleteProdutos(Long id) {
        if (!produtosRepository.existsById(id)) {
            throw new ProdutosNotFoundException(String.format("Produtos não encontrado com o id '%s'.", id));
        }
        produtosRepository.deleteById(id);
    }

}