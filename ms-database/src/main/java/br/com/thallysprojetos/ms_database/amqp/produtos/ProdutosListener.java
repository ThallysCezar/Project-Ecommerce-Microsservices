package br.com.thallysprojetos.ms_database.amqp.produtos;

import br.com.thallysprojetos.common_dtos.produto.ProdutosDTO;
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

    @RabbitListener(queues = "produtos.create.queue", containerFactory = "produtosRabbitListenerContainerFactory")
    public void handleCreateProdutosCommand(ProdutosDTO dto) {
        System.out.println("[DATABASE] Recebida mensagem de criação de produtos: " +
                "id=" + dto.getId() +
                "titulo=" + dto.getTitulo() +
                ", tipoProduto=" + dto.getTipoProduto() +
                ", descricao=" + dto.getDescricao() +
                ", preco=" + dto.getPreco() +
                ", itemEstoque=" + dto.isItemEstoque() +
                ", estoque=" + dto.getEstoque());
        try {
            Produtos produtos = modelMapper.map(dto, Produtos.class);
            produtosPersistenceService.save(produtos);
            System.out.println("[DATABASE] Produto criado com sucesso: " + produtos);
        } catch (Exception e){
            System.out.println("[DATABASE] Erro ao criar produtos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

//    @RabbitListener(queues = "produtos.create.queue", containerFactory = "produtosRabbitListenerContainerFactory")
//    public void handleCreateListProdutosCommand(List<ProdutosDTO> dto) {
//        System.out.println("[DATABASE] Recebida mensagem de criação de produtos: " +
//                "id=" + dto.getId() +
//                "titulo=" + dto.getTitulo() +
//                ", tipoProduto=" + dto.getTipoProduto() +
//                ", descricao=" + dto.getDescricao() +
//                ", preco=" + dto.getPreco() +
//                ", itemEstoque=" + dto.isItemEstoque() +
//                ", estoque=" + dto.getEstoque());
//
//        try {
//            List<ProdutosDTO> produtosDTOS.
//                    Produtos produtos = modelMapper.map(dto, Produtos.class);
//            produtosPersistenceService.save(produtos);
//            System.out.println("[DATABASE] Produto criado com sucesso: " + produtos);
//        } catch (Exception e){
//            System.out.println("[DATABASE] Erro ao criar produtos: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        }
//    }

    @RabbitListener(queues = "produtos.update.queue", containerFactory = "produtosRabbitListenerContainerFactory")
    public void handleUpdateProdutosCommand(ProdutosDTO dto) {
        System.out.println("[DATABASE] Recebida mensagem de atualização de produtos via RabbitMQ: " +
                "id=" + dto.getId() +
                "titulo=" + dto.getTitulo() +
                ", tipoProduto=" + dto.getTipoProduto() +
                ", descricao=" + dto.getDescricao() +
                ", preco=" + dto.getPreco() +
                ", itemEstoque=" + dto.isItemEstoque() +
                ", estoque=" + dto.getEstoque());
        try {
            Produtos produtos = modelMapper.map(dto, Produtos.class);
            produtosPersistenceService.update(produtos.getId(), produtos);
            System.out.println("[DATABASE] Produto atualizado com sucesso: " + produtos);
        } catch (Exception e){
            System.out.println("[DATABASE] Erro ao atualizar produto: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "produtos.delete.queue", containerFactory = "produtosRabbitListenerContainerFactory")
    public void handleDeleteProdutosCommand(Long id) {
        System.out.println("[DATABASE] Recebida mensagem de deleção de usuário via RabbitMQ. ID: " + id);
        try {
            produtosPersistenceService.deleteById(id);
            System.out.println("[DATABASE] Produto deletado com sucesso. ID: " + id);
        } catch (Exception e) {
            System.out.println("[DATABASE] Erro ao deletar produto: " + e.getMessage());
            throw e;
        }

    }

}