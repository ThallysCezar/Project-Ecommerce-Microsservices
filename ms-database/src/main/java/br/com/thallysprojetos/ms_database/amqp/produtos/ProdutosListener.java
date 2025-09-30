package br.com.thallysprojetos.ms_database.amqp.produtos;

import br.com.thallysprojetos.ms_database.dtos.produtos.ProdutosDTO;
import br.com.thallysprojetos.ms_database.dtos.usuarios.UsuariosDTO;
import br.com.thallysprojetos.ms_database.entities.Produtos;
import br.com.thallysprojetos.ms_database.services.ProdutosPersistenceService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProdutosListener {

    private final ProdutosPersistenceService produtosPersistenceService; // Serviço de persistência local
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "usuarios.create.queue")
    public void handleCreateUserCommand(UsuariosDTO dto) {
        Produtos produtos = modelMapper.map(dto, Produtos.class);
        produtosPersistenceService.save(produtos);
    }

    @RabbitListener(queues = "usuarios.update.queue")
    public void handleUpdateUserCommand(ProdutosDTO dto) {
        Produtos produtos = modelMapper.map(dto, Produtos.class);
        produtosPersistenceService.update(produtos.getId(), produtos);
    }

    @RabbitListener(queues = "usuarios.delete.queue")
    public void handleDeleteUserCommand(Long id) {
        produtosPersistenceService.deleteById(id);
    }

}