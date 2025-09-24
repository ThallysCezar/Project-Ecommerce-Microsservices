package br.com.thallysprojetos.ms_produtos.repositories;

import br.com.thallysprojetos.ms_produtos.models.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutosRepository extends JpaRepository<Produtos, Long> {
}