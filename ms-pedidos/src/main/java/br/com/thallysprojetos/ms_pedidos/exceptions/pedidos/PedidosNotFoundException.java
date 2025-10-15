package br.com.thallysprojetos.ms_pedidos.exceptions.pedidos;

public class PedidosNotFoundException extends RuntimeException {

    public PedidosNotFoundException(){
        super("Pedido não encontrado");
    }

    public PedidosNotFoundException(String message){
        super(message);
    }

}