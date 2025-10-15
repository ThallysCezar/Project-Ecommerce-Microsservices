package br.com.thallysprojetos.ms_pedidos.controllers;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.responses.ConfirmacaoPedidoResponse;
import br.com.thallysprojetos.ms_pedidos.responses.PagamentoPedidoResponse;
import br.com.thallysprojetos.ms_pedidos.responses.PedidoResponse;
import br.com.thallysprojetos.ms_pedidos.service.PedidosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidosController {

    private final PedidosService service;

    @GetMapping
    public ResponseEntity<List<PedidosDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidosDTO> findPedidoById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PedidosDTO>> findUserById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByUserId(id));
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<PedidosDTO> save(@Valid @RequestBody PedidosDTO dto, UriComponentsBuilder uriBuilder) {
//        PedidosDTO pagamento = service.createPedido(dto);
//        URI endereco = uriBuilder.path("/pedidos/{id}").buildAndExpand(pagamento.getId()).toUri();
//
//        return ResponseEntity.created(endereco).body(pagamento);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PedidoResponse> save(@Valid @RequestBody PedidosDTO dto, UriComponentsBuilder uriBuilder) {
        PedidosDTO createdPedido = service.createPedido(dto);
        
        PedidoResponse response = new PedidoResponse(
                createdPedido,
                "Pedido criado com sucesso!"
        );
        
        // Links para processar pagamento (diferentes formas de pagamento)
        Long pedidoId = createdPedido.getId() != null ? createdPedido.getId() : 1L; // Placeholder se ID ainda não definido
        
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-boleto")
                .withTitle("POST - Processar pagamento via Boleto"));
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-cartao")
                .withTitle("POST - Processar pagamento via Cartão de Crédito"));
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-pix")
                .withTitle("POST - Processar pagamento via PIX"));

        return ResponseEntity.accepted().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PedidosDTO> updatePedidos(@Valid @PathVariable Long id, @RequestBody PedidosDTO dto) {
        service.updatePedidos(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmarPedido")
    public ResponseEntity<ConfirmacaoPedidoResponse> confirmarPedidos(@PathVariable Long id) {
        service.confirmarPedidos(id);
        
        ConfirmacaoPedidoResponse response = new ConfirmacaoPedidoResponse(
                id,
                "Pedido confirmado com sucesso! Fluxo de compra finalizado.",
                "CONFIRMADO"
        );
        
        // Fim do fluxo - Sem mais links (o pedido está finalizado)
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idPedido}/pagamento")
    public ResponseEntity<PagamentoPedidoResponse> adicionarPagamentoAoPedido(@PathVariable Long idPedido, @RequestBody @Valid PagamentoDTO pagamentoDto) {
        service.processarPagamentoDoPedido(idPedido, pagamentoDto);
        
        PagamentoPedidoResponse response = new PagamentoPedidoResponse(
                idPedido,
                "Pagamento do pedido processado com sucesso!"
        );
        
        // Link para confirmar o pagamento no microsserviço de pagamentos
        Long pagamentoId = pagamentoDto.getId() != null ? pagamentoDto.getId() : 1L; // Placeholder
        response.add(Link.of("http://localhost:8082/ms-pagamentos/pagamentos/" + pagamentoId + "/confirmar", "confirmar-pagamento")
                .withTitle("PATCH - Confirmar o pagamento processado"));
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePedido(@Valid @PathVariable Long id) {
        service.deletePedidos(id);
    }

    @PatchMapping("/cancelarPedido/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarPedido(@Valid @PathVariable Long id) {
        service.cancelarPedido(id);
    }

}