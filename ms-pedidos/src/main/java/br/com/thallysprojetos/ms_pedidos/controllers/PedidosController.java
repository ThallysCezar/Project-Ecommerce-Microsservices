package br.com.thallysprojetos.ms_pedidos.controllers;

import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.responses.ConfirmacaoPedidoResponse;
import br.com.thallysprojetos.ms_pedidos.responses.PagamentoPedidoResponse;
import br.com.thallysprojetos.ms_pedidos.responses.PedidoResponse;
import br.com.thallysprojetos.ms_pedidos.security.OwnershipValidator;
import br.com.thallysprojetos.ms_pedidos.service.PedidosService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidosController {

    private final PedidosService service;
    private final OwnershipValidator ownershipValidator;

    /**
     * Lista todos os pedidos.
     * - ADMIN: retorna todos os pedidos do sistema
     * - USER: retorna apenas os pedidos do próprio usuário (filtrado automaticamente)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidosDTO>> findAll() {
        List<PedidosDTO> pedidos = service.findAll();

        // Se não for admin, filtra apenas pedidos do usuário autenticado
        if (!ownershipValidator.isAdmin()) {
            Long userId = ownershipValidator.getAuthenticatedUserId();
            pedidos = pedidos.stream()
                    .filter(p -> p.getUsuario() != null && userId.equals(p.getUsuario().getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok().body(pedidos);
    }

    /**
     * Busca um pedido específico por ID.
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidosDTO> findPedidoById(@Valid @PathVariable Long id) {
        PedidosDTO pedido = service.findById(id);

        // Valida se o usuário é dono do pedido ou admin
        if (pedido.getUsuario() != null) {
            Long pedidoUserId = pedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para visualizar este pedido");
            }
        }

        return ResponseEntity.ok().body(pedido);
    }

    /**
     * Busca todos os pedidos de um usuário específico.
     * Requer que o usuário autenticado seja o próprio usuário ou ADMIN.
     */
    @GetMapping("/user/{id}")
    @PreAuthorize("@ownershipValidator.isOwnerOrAdmin(#id)")
    public ResponseEntity<List<PedidosDTO>> findUserById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByUserId(id));
    }

    /**
     * Cria um novo pedido.
     * Qualquer usuário autenticado pode criar pedidos.
     * O pedido será automaticamente associado ao usuário que fez a requisição.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> save(@Valid @RequestBody PedidosDTO dto, UriComponentsBuilder uriBuilder) {
        PedidosDTO createdPedido = service.createPedido(dto);

        PedidoResponse response = new PedidoResponse(
                createdPedido,
                "Pedido criado com sucesso!"
        );

        // Links HATEOAS para processar pagamento (diferentes formas de pagamento)
        Long pedidoId = createdPedido.getId() != null ? createdPedido.getId() : 1L;

        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-boleto")
                .withTitle("POST - Processar pagamento via Boleto"));
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-cartao")
                .withTitle("POST - Processar pagamento via Cartão de Crédito"));
        response.add(Link.of("http://localhost:8082/ms-pedidos/pedidos/" + pedidoId + "/pagamento", "processar-pagamento-pix")
                .withTitle("POST - Processar pagamento via PIX"));

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Atualiza um pedido existente.
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidosDTO> updatePedidos(@Valid @PathVariable Long id, @RequestBody PedidosDTO dto) {
        // Valida ownership antes de atualizar
        PedidosDTO existingPedido = service.findById(id);
        if (existingPedido.getUsuario() != null) {
            Long pedidoUserId = existingPedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para atualizar este pedido");
            }
        }

        service.updatePedidos(id, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirma um pedido (finaliza o fluxo de compra).
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @PatchMapping("/{id}/confirmarPedido")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConfirmacaoPedidoResponse> confirmarPedidos(@PathVariable Long id) {
        // Valida ownership antes de confirmar
        PedidosDTO existingPedido = service.findById(id);
        if (existingPedido.getUsuario() != null) {
            Long pedidoUserId = existingPedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para confirmar este pedido");
            }
        }

        service.confirmarPedidos(id);

        ConfirmacaoPedidoResponse response = new ConfirmacaoPedidoResponse(
                id,
                "Pedido confirmado com sucesso! Fluxo de compra finalizado.",
                "CONFIRMADO"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona um pagamento a um pedido existente.
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @PostMapping("/{idPedido}/pagamento")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagamentoPedidoResponse> adicionarPagamentoAoPedido(@PathVariable Long idPedido, @RequestBody @Valid PagamentoDTO pagamentoDto) {
        // Valida ownership antes de processar pagamento
        PedidosDTO existingPedido = service.findById(idPedido);
        if (existingPedido.getUsuario() != null) {
            Long pedidoUserId = existingPedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para adicionar pagamento a este pedido");
            }
        }

        service.processarPagamentoDoPedido(idPedido, pagamentoDto);

        PagamentoPedidoResponse response = new PagamentoPedidoResponse(
                idPedido,
                "Pagamento do pedido processado com sucesso!"
        );

        // Link HATEOAS para confirmar o pagamento no microsserviço de pagamentos
        Long pagamentoId = pagamentoDto.getId() != null ? pagamentoDto.getId() : 1L;
        response.add(Link.of("http://localhost:8082/ms-pagamentos/pagamentos/" + pagamentoId + "/confirmar", "confirmar-pagamento")
                .withTitle("PATCH - Confirmar o pagamento processado"));

        return ResponseEntity.ok(response);
    }

    /**
     * Deleta um pedido.
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deletePedido(@Valid @PathVariable Long id) {
        // Valida ownership antes de deletar
        PedidosDTO existingPedido = service.findById(id);
        if (existingPedido.getUsuario() != null) {
            Long pedidoUserId = existingPedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para deletar este pedido");
            }
        }

        service.deletePedidos(id);
    }

    /**
     * Cancela um pedido.
     * Requer que o usuário seja o dono do pedido ou ADMIN.
     */
    @PatchMapping("/cancelarPedido/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void cancelarPedido(@Valid @PathVariable Long id) {
        // Valida ownership antes de cancelar
        PedidosDTO existingPedido = service.findById(id);
        if (existingPedido.getUsuario() != null) {
            Long pedidoUserId = existingPedido.getUsuario().getId();
            if (!ownershipValidator.isOwnerOrAdmin(pedidoUserId)) {
                throw new RuntimeException("Acesso negado: você não tem permissão para cancelar este pedido");
            }
        }

        service.cancelarPedido(id);
    }

}