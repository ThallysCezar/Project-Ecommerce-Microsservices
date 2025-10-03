package br.com.thallysprojetos.ms_pagamentos.configs.http;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/pagamentos")
    List<PagamentoDTO> findAll();

    @GetMapping("/pagamentos/{id}")
    Optional<PagamentoDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/pagamentos/pedido/{idPedido}")
    Optional<PagamentoDTO> findByPedidoId(@PathVariable("idPedido") Long idPedido);

    @GetMapping("/pagamentos/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

}