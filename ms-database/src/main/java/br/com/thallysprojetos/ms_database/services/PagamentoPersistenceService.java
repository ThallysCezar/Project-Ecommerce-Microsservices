package br.com.thallysprojetos.ms_database.services;

import br.com.thallysprojetos.ms_database.entities.Pagamento;
import br.com.thallysprojetos.ms_database.repositories.PagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PagamentoPersistenceService {

    private final PagamentoRepository pagamentoRepository;

    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> findById(Long id) {
        return pagamentoRepository.findById(id);
    }

    public Optional<Pagamento> findByPedidoId(Long idPedido) {
        return pagamentoRepository.findByPedidoId(idPedido);
    }

    public boolean existsById(Long id) {
        return pagamentoRepository.existsById(id);
    }

    @Transactional
    public Pagamento save(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public void deleteById(Long id) {
        pagamentoRepository.deleteById(id);
    }

}