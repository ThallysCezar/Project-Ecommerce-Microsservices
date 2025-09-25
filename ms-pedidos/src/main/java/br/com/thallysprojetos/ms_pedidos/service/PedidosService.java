package br.com.thallysprojetos.ms_pedidos.service;

import br.com.thallysprojetos.ms_pedidos.configs.https.DatabaseClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.PagamentosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.ProdutosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.UsuariosClient;
import br.com.thallysprojetos.ms_pedidos.dtos.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pedidos.dtos.enums.StatusPedidos;
import br.com.thallysprojetos.ms_pedidos.dtos.pagamentos.PagamentoDTO;
import br.com.thallysprojetos.ms_pedidos.exceptions.usuarios.PedidosNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PedidosService {

    //    private final PedidosRepository pedidosRepository;
    private final UsuariosClient usuariosClient;
    private final ProdutosClient produtosClient;
    private final PagamentosClient pagamentosClient;
    private final DatabaseClient databaseClient;
    private final ModelMapper modelMapper;

    public Page<PedidosDTO> findAll(Pageable page) {
        return databaseClient.findAll(page).map(p -> modelMapper.map(p, PedidosDTO.class));
    }

    public PedidosDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .orElseThrow(PedidosNotFoundException::new);
    }

    public List<PedidosDTO> findByUserId(Long id) {
        List<PedidosDTO> pedidos = databaseClient.findByUsuarioId(id);
        if (pedidos.isEmpty()) {
            throw new PedidosNotFoundException("Nenhum pedido encontrado para o usuário fornecido.");
        }
        return pedidos.stream()
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .collect(Collectors.toList());
    }

    public PedidosDTO createPedido(PedidosDTO dto) {
        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            usuariosClient.findById(dto.getUsuario().getId());
        } else {
            throw new RuntimeException("Usuário não fornecido.");
        }

        if (dto.getItens() != null) {
            dto.getItens().forEach(item -> {
                produtosClient.findById(item.getProduto().getId());
            });
        }

        dto.setDataHora(LocalDateTime.now());
        dto.setStatusPedidos(StatusPedidos.CRIADO);

        return databaseClient.createPedido(dto);
    }

    public void processarPagamentoDoPedido(Long idPedido, PagamentoDTO pagamentoDto) {
        PedidosDTO pedido = databaseClient.findById(idPedido)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado."));

        pagamentoDto.setPedidoId(idPedido);
        PagamentoDTO pagamentoCriado = pagamentosClient.createPayment(pagamentoDto);

        pedido.setPagamentoId(pagamentoCriado.getId());
        pedido.setStatusPedidos(StatusPedidos.AGUARDANDO_CONFIRMACAO);
        databaseClient.updatePedido(idPedido, pedido);
    }

    public PedidosDTO updatePedidos(Long id, PedidosDTO dto) {
        PedidosDTO pedidoExistente = databaseClient.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado com o ID: " + id));

        if (dto.getStatusPedidos() != null) {
            pedidoExistente.setStatusPedidos(dto.getStatusPedidos());
        }

        // A lógica de sincronização de itens deve ser movida para cá se for necessária
        // para o update de PedidosDTO.

        return databaseClient.updatePedido(id, pedidoExistente);
    }

    public void deletePedidos(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new PedidosNotFoundException(String.format("Pedidos não encontrado com o id '%s'.", id));
        }
        databaseClient.deletePedido(id);
    }

    public void confirmarPedidos(Long id) {
        PedidosDTO pedido = databaseClient.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado"));

        if (pedido.getPagamentoId() == null) {
            throw new PedidosNotFoundException("O pedido ainda não tem um pagamento associado.");
        }

        PagamentoDTO pagamento = pagamentosClient.findByPedidoId(pedido.getPagamentoId());

        if (pagamento.getStatus() == StatusPagamento.CONFIRMADO) {
            pedido.setStatusPedidos(StatusPedidos.PAGO);
        } else {
            pedido.setStatusPedidos(StatusPedidos.AGUARDANDO_CONFIRMACAO);
        }

        databaseClient.updatePedido(id, pedido);
    }

    public void cancelarPedido(Long id) {
        PedidosDTO pedido = databaseClient.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado"));

        pedido.setStatusPedidos(StatusPedidos.CANCELADO);
        databaseClient.updatePedido(id, pedido);
    }

    // A lógica de sincronização de itens deve ser ajustada para o novo contexto de DTOs.
    // Ela não pode mais trabalhar com entidades.
}