package br.com.thallysprojetos.ms_usuarios.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_usuarios")
public class Usuarios {

    private Long id;
    private String userName;
    private String email;
    private String password;

}