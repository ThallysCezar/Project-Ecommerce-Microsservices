package br.com.thallysprojetos.ms_pedidos.repositories;


import br.com.thallysprojetos.ms_pedidos.models.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidosRepository extends JpaRepository<Pedidos, Long> {

    List<Pedidos> findByUsuarioId(Long idUsuario);

}