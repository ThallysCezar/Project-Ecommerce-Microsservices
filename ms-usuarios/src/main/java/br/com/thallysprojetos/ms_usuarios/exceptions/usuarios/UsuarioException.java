package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsuarioException extends ResponseStatusException {

    public UsuarioException(){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Erro geral model");
    }

    public UsuarioException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
