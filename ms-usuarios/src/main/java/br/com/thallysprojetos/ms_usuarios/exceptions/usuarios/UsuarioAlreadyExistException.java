package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

public class UsuarioAlreadyExistException extends RuntimeException {

    public UsuarioAlreadyExistException(){
        super("Já existe um usuário com esse email");
    }

    public UsuarioAlreadyExistException(String message){
        super(message);
    }

}