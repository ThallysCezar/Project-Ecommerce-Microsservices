package br.com.thallysprojetos.ms_pedidos.service.impl;

import br.com.thallysprojetos.common_dtos.enums.StatusPagamento;
import br.com.thallysprojetos.common_dtos.enums.StatusPedidos;
import br.com.thallysprojetos.common_dtos.pagamento.PagamentoDTO;
import br.com.thallysprojetos.common_dtos.pedido.PedidosDTO;
import br.com.thallysprojetos.ms_pedidos.configs.https.DatabaseClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.ProdutosClient;
import br.com.thallysprojetos.ms_pedidos.configs.https.UsuariosClient;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosAlreadyExistException;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosNotFoundException;
import br.com.thallysprojetos.ms_pedidos.service.PedidosService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PedidosServiceImpl implements PedidosService {

    private final UsuariosClient usuariosClient;
    private final ProdutosClient produtosClient;
    private final DatabaseClient databaseClient;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<PedidosDTO> findAll() {
        return databaseClient.findAll();
    }

    @Override
    public PedidosDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .orElseThrow(PedidosNotFoundException::new);
    }

    @Override
    public List<PedidosDTO> findByUserId(Long id) {
        List<PedidosDTO> pedidos = databaseClient.findByUsuarioId(id);
        if (pedidos.isEmpty()) {
            throw new PedidosNotFoundException("Nenhum pedido encontrado para o usuário fornecido.");
        }
        return pedidos.stream()
                .map(p -> modelMapper.map(p, PedidosDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PedidosDTO createPedido(PedidosDTO dto) {
        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            usuariosClient.findById(dto.getUsuario().getId());
            verificationPendingOrders(dto.getUsuario().getId());
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

        if (dto.getPagamento() != null) {
            dto.getPagamento().setStatus(StatusPagamento.CRIADO);
            dto.getPagamento().setPedidoId(dto.getId());
            System.out.println("[PEDIDOS] Enviando comando de CRIAÇÃO de pagamento para RabbitMQ: UsuarioID=" + dto.getUsuario().getId());
            rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.create", dto.getPagamento());
        }
        System.out.println("[PEDIDOS] Enviando comando de CRIAÇÃO de pedido para RabbitMQ: UsuarioID=" + dto.getUsuario().getId());
        rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.create", dto);
        return dto;
    }

    @Override
    public void processarPagamentoDoPedido(Long idPedido, PagamentoDTO pagamentoDto) {
        if (pagamentoDto == null) {
            throw new RuntimeException("Dados do pagamento não fornecidos.");
        }
        pagamentoDto.setPedidoId(idPedido);
        pagamentoDto.setStatus(StatusPagamento.PROCESSADO);
        System.out.println("[PEDIDOS] Enviando comando de PROCESSAMENTO de pagamento para RabbitMQ: PedidoID=" + idPedido);
        rabbitTemplate.convertAndSend("pagamentos.exchange", "pagamentos.create", pagamentoDto);
    }

    @Override
    public void updatePedidos(Long id, PedidosDTO dto) {
        System.out.println("[PEDIDOS] Enviando comando de ATUALIZAÇÃO de pedido para RabbitMQ: PedidoID=" + id);
        try {
            System.out.println("[PEDIDOS] Enviando comando de ATUALIZAÇÃO de pedido para RabbitMQ: PedidoID=" + id);
            rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.update", dto);
        } catch (Exception e) {
            System.out.println("[PEDIDOS] Erro ao enviar mensagem de atualização para RabbitMQ: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public void deletePedidos(Long id) {
        System.out.println("[PEDIDOS] Enviando comando de EXCLUSÃO de pedido para RabbitMQ: PedidoID=" + id);
        rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.delete", id);
    }

    @Override
    public void confirmarPedidos(Long id) {
        System.out.println("[PEDIDOS] Enviando comando de CONFIRMAÇÃO de pedido para RabbitMQ: PedidoID=" + id);
        rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.confirm", id);
    }

    @Override
    public void cancelarPedido(Long id) {
        System.out.println("[PEDIDOS] Enviando comando de CANCELAMENTO de pedido para RabbitMQ: PedidoID=" + id);
        rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.cancel", id);
    }

    private void verificationPendingOrders(Long usuarioId) {
        try {
            List<PedidosDTO> pendingOrders = databaseClient.findPendingByUsuarioId(usuarioId);
            if (!pendingOrders.isEmpty()) {
                throw new PedidosAlreadyExistException(
                        "O usuário já possui " + pendingOrders.size() +
                                " pedido(s) pendente(s). Finalize-o(s) antes de criar um novo pedido."
                );
            }
        } catch (FeignException.NotFound e) {
            log.info("Nenhum pedido pendente encontrado para o usuário ID {}. Validação passou com sucesso.", usuarioId);
        }
    }

}