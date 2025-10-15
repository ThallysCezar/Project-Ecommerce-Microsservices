package br.com.thallysprojetos.ms_pedidos.exceptions.pedidos;

public class PedidosException extends RuntimeException {

    public PedidosException(){
        super("Erro ao processar pedido");
    }

    public PedidosException(String message){
        super(message);
    }

}
