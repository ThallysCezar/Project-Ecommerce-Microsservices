package br.com.thallysprojetos.ms_produtos.services;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;

import java.util.List;

public interface ProdutosService {

    List<ProdutosDTO> findAll();

    ProdutosDTO findProductById(Long id);

    ProdutosDTO createProduct(ProdutosDTO dto);

    List<ProdutosDTO> createProducts(List<ProdutosDTO> dtos);

    ProdutosDTO updateProdutos(Long id, ProdutosDTO dto);

    void deleteProdutos(Long id);

}
