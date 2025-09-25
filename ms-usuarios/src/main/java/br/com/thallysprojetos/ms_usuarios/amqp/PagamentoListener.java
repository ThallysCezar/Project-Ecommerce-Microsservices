package br.com.thallysprojetos.ms_usuarios.amqp;

import org.springframework.stereotype.Component;

@Component
public class PagamentoListener {

//    @RabbitListener(queues = "pagamentos.detalhes-pedidos")
//    public void recebeMensagens(@Payload PagamentoDTO pagamento){
//
//        String mensagem = """
//                Dados do pagamento: %s
//                Número do pedidos: %s
//                Valor R$: %s
//                Status: %s
//                """.formatted(pagamento.getId(), pagamento.getPedidoId(), pagamento.getValor(), pagamento.getValor());
//
//        System.out.println("Recebi a mensagem " + mensagem);
//    }

}