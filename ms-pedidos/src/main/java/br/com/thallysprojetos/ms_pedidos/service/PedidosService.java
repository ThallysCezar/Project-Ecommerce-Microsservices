package br.com.thallysprojetos.ms_pedidos.service;

import br.com.thallysprojetos.ms_pedidos.configs.https.DatabaseClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.PagamentosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.ProdutosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.UsuariosClient;
import br.com.thallysprojetos.ms_pedidos.dtos.ItemDoPedidoDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.dtos.pagamentos.PagamentoDTO;
import br.com.thallysprojetos.ms_pedidos.exceptions.usuarios.PedidosNotFoundException;
import br.com.thallysprojetos.ms_pedidos.models.ItemDoPedido;
import br.com.thallysprojetos.ms_pedidos.models.Pedidos;
import br.com.thallysprojetos.ms_pedidos.models.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pedidos.models.enums.StatusPedidos;
import br.com.thallysprojetos.ms_pedidos.repositories.PedidosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PedidosService {

    private final PedidosRepository pedidosRepository;
    private final UsuariosClient usuariosClient;
    private final ProdutosClient produtosClient;
    private final PagamentosClient pagamentoClient;
    private final DatabaseClient databaseClient;
    private final ModelMapper modelMapper;

    public Page<PedidosDTO> findAll(Pageable page) {
        return pedidosRepository.findAll(page).map(p -> modelMapper.map(p, PedidosDTO.class));
    }

    public PedidosDTO findById(Long id) {
        return pedidosRepository.findById(id)
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .orElseThrow(PedidosNotFoundException::new);
    }

    public List<PedidosDTO> findByUserId(Long id) {
        List<Pedidos> pedidos = pedidosRepository.findByUsuarioId(id);
        if (pedidos.isEmpty()) {
            throw new PedidosNotFoundException("Nenhum pedido encontrado para o usuário fornecido.");
        }
        return pedidos.stream()
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidosDTO createPedido(PedidosDTO dto) {
        usuariosClient.findById(dto.getUsuario().getId());

        Pedidos novoPedido = modelMapper.map(dto, Pedidos.class);
        novoPedido.setUsuarioId(dto.getUsuario().getId());
        novoPedido.setStatusPedidos(StatusPedidos.CRIADO);
        novoPedido.setDataHora(LocalDateTime.now());

        if (novoPedido.getItens() != null) {
            novoPedido.getItens().forEach(item -> {
                item.setProdutoId(produtosClient.findById(item.getProdutoId()).getId());
                item.setPedido(novoPedido);
            });
        }

        Pedidos pedidoSalvo = pedidosRepository.save(novoPedido);
        return modelMapper.map(pedidoSalvo, PedidosDTO.class);
    }

    @Transactional
    public void processarPagamentoDoPedido(Long idPedido, PagamentoDTO pagamentoDto) {
        Pedidos pedido = pedidosRepository.findById(idPedido)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado."));

        PagamentoDTO pagamentoCriado = pagamentoClient.createPayment(pagamentoDto);

        pedido.setPagamentoId(pagamentoCriado.getId());
        pedido.setStatusPedidos(StatusPedidos.AGUARDANDO_CONFIRMACAO);
        pedidosRepository.save(pedido);
    }

    @Transactional
    public PedidosDTO updatePedidos(Long id, PedidosDTO dto) {
        Pedidos pedidoExistente = pedidosRepository.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado com o ID: " + id));

        if (dto.getStatusPedidos() != null) {
            pedidoExistente.setStatusPedidos(dto.getStatusPedidos());
        }

        if (dto.getItens() != null) {
            sincronizarItensDoPedido(pedidoExistente, dto.getItens());
        }

        Pedidos pedidoAtualizado = pedidosRepository.save(pedidoExistente);
        return modelMapper.map(pedidoAtualizado, PedidosDTO.class);
    }

    @Transactional
    public void deletePedidos(Long id) {
        if (!pedidosRepository.existsById(id)) {
            throw new PedidosNotFoundException(String.format("Pedidos não encontrado com o id '%s'.", id));
        }
        pedidosRepository.deleteById(id);
    }

    public void confirmarPedidos(Long id) {
        Pedidos pedido = pedidosRepository.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado"));

        PagamentoDTO pagamento = pagamentoClient.findByPedidoId(id);

        if (pagamento.getStatus() == StatusPagamento.CONFIRMADO) {
            pedido.setStatusPedidos(StatusPedidos.PAGO);
        } else {
            pedido.setStatusPedidos(StatusPedidos.AGUARDANDO_CONFIRMACAO);
        }
        pedidosRepository.save(pedido);
    }

    public void cancelarPedido(Long id) {
        Pedidos pedido = pedidosRepository.findById(id)
                .orElseThrow(() -> new PedidosNotFoundException("Pedido não encontrado"));

        pedido.setStatusPedidos(StatusPedidos.CANCELADO);
        pedidosRepository.save(pedido);
    }

    private void sincronizarItensDoPedido(Pedidos pedidoExistente, List<ItemDoPedidoDTO> itensDto) {
        Map<Long, ItemDoPedido> itensExistentesMap = pedidoExistente.getItens().stream()
                .collect(Collectors.toMap(ItemDoPedido::getId, item -> item));

        Map<Long, ItemDoPedido> itensParaManter = new HashMap<>();

        for (ItemDoPedidoDTO itemDto : itensDto) {
            if (itemDto.getId() != null && itensExistentesMap.containsKey(itemDto.getId())) {
                ItemDoPedido itemParaAtualizar = itensExistentesMap.get(itemDto.getId());
                itemParaAtualizar.setQuantidade(itemDto.getQuantidade());
                itemParaAtualizar.setDescricao(itemDto.getDescricao());

                // A referência ao produto não muda, então não precisa ser buscada novamente
                itemParaAtualizar.setProdutoId(itemDto.getProduto().getId());

                itensParaManter.put(itemParaAtualizar.getId(), itemParaAtualizar);
            } else if (itemDto.getId() == null) {
                produtosClient.findById(itemDto.getProduto().getId());

                ItemDoPedido novoItem = modelMapper.map(itemDto, ItemDoPedido.class);
                novoItem.setProdutoId(itemDto.getProduto().getId());
                novoItem.setPedido(pedidoExistente);

                pedidoExistente.getItens().add(novoItem);
            }
        }
        pedidoExistente.getItens().removeIf(item -> !itensParaManter.containsKey(item.getId()));
    }

}