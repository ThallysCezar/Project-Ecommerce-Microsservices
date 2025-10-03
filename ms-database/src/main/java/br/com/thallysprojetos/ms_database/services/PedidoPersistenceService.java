package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.common_dtos.enums.StatusPedidos;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.repositories.PedidosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PedidoPersistenceService {
    private final PagamentoPersistenceService pagamentoService;
    private final ModelMapper modelMapper;

    private final PedidosRepository pedidosRepository;

    public List<PedidosDTO> findAll() {
        List<Pedidos> pedidos = pedidosRepository.findAll();
        return pedidos.stream().map(pedido -> {
            PedidosDTO dto = modelMapper.map(pedido, PedidosDTO.class);
            if (pedido.getPagamentoId() != null) {
                pagamentoService.findById(pedido.getPagamentoId()).ifPresent(pagamento -> {
                    dto.setPagamento(modelMapper.map(pagamento, PagamentoDTO.class));
                });
            }
            return dto;
        }).toList();
    }

    public Optional<Pedidos> findById(Long id) {
        return pedidosRepository.findById(id);
    }

    public List<Pedidos> findByUsuarioId(Long usuarioId) {
        return pedidosRepository.findByUsuarioId(usuarioId);
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