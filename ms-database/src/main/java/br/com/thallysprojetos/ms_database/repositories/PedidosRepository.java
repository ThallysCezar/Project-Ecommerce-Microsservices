package br.com.thallysprojetos.ms_database.repositories;

import br.com.thallysprojetos.ms_database.entities.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidosRepository extends JpaRepository<Pedidos, Long> {

    List<Pedidos> findByUsuarioId(Long idUsuario);

}