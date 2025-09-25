package br.com.thallysprojetos.ms_pagamentos.configs.http;

import br.com.thallysprojetos.ms_pagamentos.dtos.PagamentoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "ms-database")
public interface DatabaseClient {

    @GetMapping("/pagamentos")
    Page<PagamentoDTO> findAll(Pageable page);

    @GetMapping("/pagamentos/{id}")
    Optional<PagamentoDTO> findById(@PathVariable("id") Long id);

    @GetMapping("/pagamentos/pedido/{idPedido}")
    Optional<PagamentoDTO> findByPedidoId(@PathVariable("idPedido") Long idPedido);

    @GetMapping("/pagamentos/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    @PostMapping("/pagamentos")
    PagamentoDTO createPagamento(@RequestBody PagamentoDTO dto);

    @PutMapping("/pagamentos/{id}")
    PagamentoDTO updatePagamento(@PathVariable("id") Long id, @RequestBody PagamentoDTO dto);

    @DeleteMapping("/pagamentos/{id}")
    void deletePagamento(@PathVariable("id") Long id);

}
