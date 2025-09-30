package br.com.thallysprojetos.ms_database.amqp.usuarios;

import br.com.thallysprojetos.ms_database.dtos.usuarios.UsuariosDTO;
import br.com.thallysprojetos.ms_database.entities.Usuarios;
import br.com.thallysprojetos.ms_database.services.UsuariosPersistenceService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UsuariosListener {

    private final UsuariosPersistenceService usuarioPersistenceService; // Serviço de persistência local
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "usuarios.create.queue")
    @Transactional
    public void handleCreateUserCommand(@Payload UsuariosDTO dto) {
        Usuarios usuario = modelMapper.map(dto, Usuarios.class);
        usuarioPersistenceService.save(usuario);
    }

    @RabbitListener(queues = "usuarios.update.queue")
    @Transactional
    public void handleUpdateUserCommand(UsuariosDTO dto) {
        Usuarios usuario = modelMapper.map(dto, Usuarios.class);
        usuarioPersistenceService.update(usuario);
    }

    @RabbitListener(queues = "usuarios.delete.queue")
    @Transactional
    public void handleDeleteUserCommand(Long id) {
        usuarioPersistenceService.deleteById(id);
    }

}