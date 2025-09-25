package br.com.thallysprojetos.ms_pedidos.dtos;

import br.com.thallysprojetos.ms_pedidos.dtos.enums.StatusPedidos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PedidosDTO {

    private Long id;
    private LocalDateTime dataHora;
    private StatusPedidos statusPedidos;
    private List<ItemDoPedidoDTO> itens;
    private UsuarioIdDTO usuario;
    private Long pagamentoId;

}