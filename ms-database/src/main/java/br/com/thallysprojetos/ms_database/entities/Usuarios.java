package br.com.thallysprojetos.ms_database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
public class Usuarios {

    private Long id;
    private String userName;
    private String email;
    private String password;

}