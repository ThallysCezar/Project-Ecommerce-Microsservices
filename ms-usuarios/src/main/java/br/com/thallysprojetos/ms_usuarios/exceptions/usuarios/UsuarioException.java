package br.com.thallysprojetos.ms_usuarios.exceptions.usuarios;

public class UsuarioException extends RuntimeException {

    public UsuarioException(){
        super("Erro ao processar usuário");
    }

    public UsuarioException(String message){
        super(message);
    }

}
