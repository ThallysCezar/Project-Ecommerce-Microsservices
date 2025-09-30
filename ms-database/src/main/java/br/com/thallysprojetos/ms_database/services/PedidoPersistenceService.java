package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.repositories.PedidosRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PedidoPersistenceService {

    private final PedidosRepository pedidosRepository;

    // --- MÉTODOS DE LEITURA ---

    public List<Pedidos> findAll() {
        return pedidosRepository.findAll();
    }

    public Optional<Pedidos> findById(Long id) {
        return pedidosRepository.findById(id);
    }

    public List<Pedidos> findByUsuarioId(Long usuarioId) {
        // Assume que o Repositório possui findByUsuarioId
        return pedidosRepository.findByUsuarioId(usuarioId);
    }

    public boolean existsById(Long id) {
        return pedidosRepository.existsById(id);
    }


    // --- MÉTODOS DE ESCRITA ---

    @Transactional
    public Pedidos save(Pedidos pedidos){
        // Usado para CREATE e UPDATE. O JPA lida com a inserção/atualização.
        return pedidosRepository.save(pedidos);
    }

    @Transactional
    public void deleteById(Long id) {
        pedidosRepository.deleteById(id);
    }

}
