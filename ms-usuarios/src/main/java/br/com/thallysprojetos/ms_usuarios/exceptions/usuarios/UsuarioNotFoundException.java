package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsuarioNotFoundException extends ResponseStatusException {

    public UsuarioNotFoundException(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Usuario não encontrado com esse id");
    }

    public UsuarioNotFoundException(String message){
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

}