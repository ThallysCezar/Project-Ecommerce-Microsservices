package br.com.thallysprojetos.ms_pedidos.dtos.pagamentos;

import br.com.thallysprojetos.ms_pedidos.models.enums.StatusPagamento;
import br.com.thallysprojetos.ms_pedidos.models.enums.TipoFormaPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagamentoDTO {

    private Long id;

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