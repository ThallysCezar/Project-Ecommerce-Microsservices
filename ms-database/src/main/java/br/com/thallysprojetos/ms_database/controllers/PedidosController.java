package br.com.thallysprojetos.ms_database.controllers;

import br.com.thallysprojetos.common_dtos.pedido.ItemDoPedidoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.common_dtos.pedido.ProdutoIdDTO;
import br.com.thallysprojetos.common_dtos.pedido.UsuarioIdDTO;
import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.services.PedidoPersistenceService;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.ms_database.services.PagamentoPersistenceService;
import org.modelmapper.ModelMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidosController {

    private final PedidoPersistenceService service;
    private final PagamentoPersistenceService pagamentoService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<PedidosDTO>> findAll() {
        List<Pedidos> pedidos = service.findAll();
        List<PedidosDTO> dtos = pedidos.stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    private PedidosDTO toDTO(Pedidos pedido) {
        PedidosDTO dto = new PedidosDTO();
        dto.setId(pedido.getId());
        dto.setDataHora(pedido.getDataHora());
        dto.setStatusPedidos(pedido.getStatusPedidos());
        dto.setUsuario(new UsuarioIdDTO(pedido.getUsuarioId()));
        if (pedido.getItens() != null) {
            dto.setItens(pedido.getItens().stream().map(item -> {
                ItemDoPedidoDTO itemDTO = new ItemDoPedidoDTO();
                itemDTO.setId(item.getId());
                itemDTO.setQuantidade(item.getQuantidade());
                itemDTO.setDescricao(item.getDescricao());
                itemDTO.setProduto(new ProdutoIdDTO(item.getProdutoId()));
                return itemDTO;
            }).toList());
        }
        // Preencher o campo pagamento do DTO
        if (pedido.getPagamentoId() != null) {
            pagamentoService.findById(pedido.getPagamentoId()).ifPresent(pagamento -> {
                PagamentoDTO pagamentoDTO = modelMapper.map(pagamento, PagamentoDTO.class);
                dto.setPagamento(pagamentoDTO);
            });
        }
        return dto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedidos> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<List<Pedidos>> findByUserId(@PathVariable Long usuarioId) {
        List<Pedidos> pedidos = service.findByUsuarioId(usuarioId);
        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedidos);
    }

}