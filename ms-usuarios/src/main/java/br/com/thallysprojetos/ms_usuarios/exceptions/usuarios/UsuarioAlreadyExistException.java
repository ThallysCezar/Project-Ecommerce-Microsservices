package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsuarioAlreadyExistException extends ResponseStatusException {

    public UsuarioAlreadyExistException(){
        super(HttpStatus.CONFLICT, "Já existe um usuário com esse email");
    }

    public UsuarioAlreadyExistException(String message){
        super(HttpStatus.CONFLICT, message);
    }

}