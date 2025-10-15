package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(){
        super("Usuário não encontrado");
    }

    public UsuarioNotFoundException(String message){
        super(message);
    }

}