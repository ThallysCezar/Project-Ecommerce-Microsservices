package br.com.thallysprojetos.ms_database.repositories;

import br.com.thallysprojetos.ms_pagamentos.models.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByPedidoId(Long pedidoId);

}