package br.com.thallysprojetos.ms_pagamentos.controllers;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_pagamentos.responses.ConfirmacaoPagamentoResponse;
import br.com.thallysprojetos.ms_pagamentos.services.PagamentoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@AllArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PagamentoDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagamentoDTO> findPagamentoById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagamentoDTO> findPagamentoByPedido(@Valid @PathVariable Long idPedido) {
        PagamentoDTO pagamento = service.findByPedidoId(idPedido);
        return ResponseEntity.ok().body(pagamento);
    }

    @PostMapping("/pedido/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagamentoDTO> createPayment(@RequestBody @Valid PagamentoDTO dto) {
        PagamentoDTO pagamentoCriado = service.createPayment(dto);
        return new ResponseEntity<>(pagamentoCriado, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagamentoDTO> updatePagamento(@Valid @PathVariable Long id, @RequestBody PagamentoDTO dto) {
        service.updatePagamento(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfirmacaoPagamentoResponse> confirmarPagamento(@PathVariable Long id) {
        service.processarPagamento(id);
        
        // Buscar o pagamento para pegar o pedidoId
        PagamentoDTO pagamento = service.findById(id);
        
        ConfirmacaoPagamentoResponse response = new ConfirmacaoPagamentoResponse(
                id,
                pagamento.getPedidoId(),
                "Pagamento confirmado com sucesso!",
                "CONFIRMADO"
        );
        
        // Link para confirmar o pedido (último passo do fluxo)
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pagamento.getPedidoId() + "/confirmarPedido", "confirmar-pedido")
                .withTitle("PATCH - Confirmar o pedido (finalizar compra)"));
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePagamento(@Valid @PathVariable Long id) {
        service.deletePagamento(id);
    }

}