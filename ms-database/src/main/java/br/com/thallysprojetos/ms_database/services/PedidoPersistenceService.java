package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.common_dtos.enums.StatusPedidos;
import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.repositories.PedidosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PedidoPersistenceService {

    private final PedidosRepository pedidosRepository;

    public List<Pedidos> findAll() {
        return pedidosRepository.findAll();
    }

    public Optional<Pedidos> findById(Long id) {
        return pedidosRepository.findById(id);
    }

    public List<Pedidos> findByUsuarioId(Long usuarioId) {
        return pedidosRepository.findByUsuarioId(usuarioId);
    }

    public boolean existsById(Long id) {
        return pedidosRepository.existsById(id);
    }

    @Transactional
    public Pedidos save(Pedidos pedidos) {
        return pedidosRepository.save(pedidos);
    }

    @Transactional
    public void deleteById(Long id) {
        pedidosRepository.deleteById(id);
    }

    public void confirmarPedidos(Long id) {
        Optional<Pedidos> pedidoOpt = pedidosRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedidos pedido = pedidoOpt.get();
            pedido.setStatusPedidos(StatusPedidos.CONFIRMADO);
            pedidosRepository.save(pedido);
            System.out.println("[PedidoPersistenceService] Pedido confirmado: ID=" + id);
        } else {
            System.out.println("[PedidoPersistenceService] Pedido não encontrado para confirmação: ID=" + id);
        }
    }

    @Transactional
    public Pedidos updatePedido(Long id, br.com.thallysprojetos.common_dtos.pedido.PedidosDTO dto) {
        Optional<Pedidos> pedidoOpt = pedidosRepository.findById(id);
        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }
        Pedidos pedido = pedidoOpt.get();
        pedido.setDataHora(dto.getDataHora());
        pedido.setStatusPedidos(dto.getStatusPedidos());
        pedido.setUsuarioId(dto.getUsuario().getId());
        // Atualiza itens
        pedido.getItens().clear();
        if (dto.getItens() != null) {
            dto.getItens().forEach(itemDTO -> {
                var item = new br.com.thallysprojetos.ms_database.entities.ItemDoPedido();
                item.setId(itemDTO.getId());
                item.setQuantidade(itemDTO.getQuantidade());
                item.setDescricao(itemDTO.getDescricao());
                item.setProdutoId(itemDTO.getProduto().getId());
                item.setPedido(pedido);
                pedido.getItens().add(item);
            });
        }
        // Atualiza pagamentoId se vier no DTO
        if (dto.getPagamento() != null && dto.getPagamento().getId() != null) {
            pedido.setPagamentoId(dto.getPagamento().getId());
        }
        return pedidosRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Optional<Pedidos> pedidoOpt = pedidosRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedidos pedido = pedidoOpt.get();
            pedido.setStatusPedidos(StatusPedidos.CANCELADO);
            pedidosRepository.save(pedido);
            System.out.println("[PedidoPersistenceService] Pedido cancelado: ID=" + id);
        } else {
            System.out.println("[PedidoPersistenceService] Pedido não encontrado para cancelamento: ID=" + id);
        }
    }

}