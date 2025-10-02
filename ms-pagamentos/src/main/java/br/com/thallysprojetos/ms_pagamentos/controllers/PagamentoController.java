package br.com.thallysprojetos.ms_pagamentos.controllers;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.services.PagamentoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@AllArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @GetMapping
    public ResponseEntity<List<PagamentoDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> findPagamentoById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<PagamentoDTO> findPagamentoByPedido(@Valid @PathVariable Long idPedido) {
        PagamentoDTO pagamento = service.findByPedidoId(idPedido);
        return ResponseEntity.ok().body(pagamento);
    }

    @PostMapping("/pedido/create")
    public ResponseEntity<PagamentoDTO> createPayment(@RequestBody @Valid PagamentoDTO dto) {
        PagamentoDTO pagamentoCriado = service.createPayment(dto);
        return new ResponseEntity<>(pagamentoCriado, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PagamentoDTO> updatePagamento(@Valid @PathVariable Long id, @RequestBody PagamentoDTO dto) {
        service.updatePagamento(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    public void confirmarPagamento(@PathVariable Long id) {
        service.processarPagamento(id);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePagamento(@Valid @PathVariable Long id) {
        service.deletePagamento(id);
    }

}