package br.com.thallysprojetos.ms_pedidos.exceptions.pedidos;

public class PedidosAlreadyExistException extends RuntimeException {

    public PedidosAlreadyExistException(){
        super("Já existe esse pedido");
    }

    public PedidosAlreadyExistException(String message){
        super(message);
    }

}