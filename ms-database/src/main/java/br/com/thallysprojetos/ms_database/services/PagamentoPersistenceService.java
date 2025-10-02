package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.ms_database.entities.Pagamento;
import br.com.thallysprojetos.ms_database.repositories.PagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PagamentoPersistenceService {

    private final PagamentoRepository pagamentoRepository;

    // --- MÉTODOS DE LEITURA (PARA O CONTROLLER) ---

    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> findById(Long id) {
        return pagamentoRepository.findById(id);
    }

    // Método que assume que o repositório tem o findByPedidoId
    public Optional<Pagamento> findByPedidoId(Long idPedido) {
        // Assume que PagamentoRepository tem um método findByPedidoId(Long)
        return pagamentoRepository.findByPedidoId(idPedido);
    }

    @Transactional
    public Pagamento save(Pagamento pagamento) {
        // Usado para criar ou atualizar (persiste a entidade mapeada)
        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public void deleteById(Long id) {
        pagamentoRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return pagamentoRepository.existsById(id);
    }

}