package br.com.thallysprojetos.ms_database.amqp.pedidos;

import br.com.thallysprojetos.ms_database.dtos.PedidosDTO;
import br.com.thallysprojetos.ms_database.entities.Pedidos;
import br.com.thallysprojetos.ms_database.services.PedidoPersistenceService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PedidosListener {

    private final PedidoPersistenceService pedidoPersistenceService;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "pedidos.create.queue")
    public void createPedido(PedidosDTO dto){
        System.out.println("Comando de criação de pedido recebido: " + dto.getId());

        Pedidos pedido = modelMapper.map(dto, Pedidos.class);
        pedidoPersistenceService.save(pedido);
    }

}