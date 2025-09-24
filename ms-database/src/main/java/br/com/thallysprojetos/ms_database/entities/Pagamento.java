package br.com.thallysprojetos.ms_database.entities;

import br.com.thallysprojetos.ms_database.entities.enums.StatusPagamento;
import br.com.thallysprojetos.ms_database.entities.enums.TipoFormaPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "pedido_id")
    private Long pedidoId;

}