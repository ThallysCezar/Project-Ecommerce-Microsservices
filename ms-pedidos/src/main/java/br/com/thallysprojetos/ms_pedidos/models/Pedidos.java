package br.com.thallysprojetos.ms_pedidos.models;

import br.com.thallysprojetos.ms_pedidos.models.enums.StatusPedidos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_pedidos")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusPedidos statusPedidos;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itens = new ArrayList<>();

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "pagamento_id")
    private Long pagamentoId;

}