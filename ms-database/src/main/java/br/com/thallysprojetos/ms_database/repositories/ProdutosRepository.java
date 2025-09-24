package br.com.thallysprojetos.ms_database.repositories;

import br.com.thallysprojetos.ms_database.entities.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutosRepository extends JpaRepository<Produtos, Long> {
}