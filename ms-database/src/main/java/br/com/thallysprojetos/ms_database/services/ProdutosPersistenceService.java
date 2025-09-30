package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.ms_database.entities.Produtos;
import br.com.thallysprojetos.ms_database.repositories.ProdutosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProdutosPersistenceService {

    private final ProdutosRepository produtosRepository;

    public List<Produtos> findAll() {
        return produtosRepository.findAll();
    }

    public Optional<Produtos> findById(Long id) {
        return produtosRepository.findById(id);
    }

    @Transactional
    public Produtos save(Produtos produtos) {
        return produtosRepository.save(produtos);
    }

    @Transactional
    public void update(Long id, Produtos produtos) {
        produtosRepository.saveAndFlush(produtos);
    }

    @Transactional
    public void deleteById(Long id) {
        produtosRepository.deleteById(id);
    }

}