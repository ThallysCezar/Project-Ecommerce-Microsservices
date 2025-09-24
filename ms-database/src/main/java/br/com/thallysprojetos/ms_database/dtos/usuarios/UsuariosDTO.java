package br.com.thallysprojetos.ms_database.dtos.usuarios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuariosDTO {

    private Long id;
    private String userName;
    private String email;
    private String password;

}