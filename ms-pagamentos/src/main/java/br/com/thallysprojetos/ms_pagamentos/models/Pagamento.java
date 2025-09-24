package br.com.thallysprojetos.ms_pagamentos.models;

import br.com.thallysprojetos.ms_pagamentos.models.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.models.enums.TipoFormaPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_pagamentos")
@Getter
@Setter
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double valor;

    @NotNull
    private StatusPagamento status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoFormaPagamento tipoPagamento;

    private String nomeTitularCartao;
    private String numeroCartao;
    private String expiracaoCartao;
    private String codigoCartao;

    private String codigoDeBarrasBoleto;

    private String chavePix;

    // Aqui não tem os pedidos, pois se trata de um outro microsserviço
    @Column(name = "pedido_id")
    private Long pedidoId;


}