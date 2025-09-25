package br.com.thallysprojetos.ms_pagamentos.dtos;

import br.com.thallysprojetos.ms_pagamentos.dtos.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pagamentos.dtos.enums.TipoFormaPagamento;
import br.com.thallysprojetos.ms_pagamentos.utils.ValidacaoPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ValidacaoPagamento
@Getter
@Setter
public class PagamentoDTO {

    @NotNull
    private Double valor;

    @NotNull
    private TipoFormaPagamento tipoPagamento;

    private StatusPagamento status;

    private String nomeTitularCartao;
    private String numeroCartao;
    private String expiracaoCartao;
    private String codigoCartao;

    private String codigoDeBarrasBoleto;

    private String chavePix;

}