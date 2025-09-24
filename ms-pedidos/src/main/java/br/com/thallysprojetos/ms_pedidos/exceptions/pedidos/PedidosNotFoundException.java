package br.com.thallysprojetos.ms_pedidos.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PedidosNotFoundException extends ResponseStatusException {

    public PedidosNotFoundException(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Pedido não encontrado com esse id");
    }

    public PedidosNotFoundException(String message){
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}