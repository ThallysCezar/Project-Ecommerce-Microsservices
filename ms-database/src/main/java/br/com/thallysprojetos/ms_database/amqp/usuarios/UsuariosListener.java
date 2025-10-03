package br.com.thallysprojetos.ms_database.amqp.usuarios;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_database.entities.Usuarios;
import br.com.thallysprojetos.ms_database.services.UsuariosPersistenceService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UsuariosListener {

    private final UsuariosPersistenceService usuarioPersistenceService;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "usuarios.create.queue", containerFactory = "usuariosRabbitListenerContainerFactory")
    @Transactional
    public void handleCreateUserCommand(UsuariosDTO dto) {
        System.out.println("[DATABASE] Recebida mensagem de criação de usuário: " +
                "nome=" + dto.getUserName() +
                ", email=" + dto.getEmail() +
                ", id=" + dto.getId());
        try {
            Usuarios usuario = modelMapper.map(dto, Usuarios.class);
            usuarioPersistenceService.save(usuario);
            System.out.println("[DATABASE] Usuário criado com sucesso: " + usuario);
        } catch (Exception e) {
            System.out.println("[DATABASE] Erro ao criar usuário: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @RabbitListener(queues = "usuarios.update.queue", containerFactory = "usuariosRabbitListenerContainerFactory")
    @Transactional
    public void handleUpdateUserCommand(UsuariosDTO dto) {
        System.out.println("[DATABASE] Recebida mensagem de atualização de usuário via RabbitMQ: " +
                "nome=" + dto.getUserName() +
                ", email=" + dto.getEmail() +
                ", id=" + dto.getId());

        try {
            Usuarios usuario = modelMapper.map(dto, Usuarios.class);
            usuarioPersistenceService.save(usuario);
            System.out.println("[DATABASE] Usuário atualizado com sucesso: " + usuario);
        } catch (Exception e) {
            System.out.println("[DATABASE] Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "usuarios.delete.queue", containerFactory = "usuariosRabbitListenerContainerFactory")
    @Transactional
    public void handleDeleteUserCommand(Long id) {
        System.out.println("[DATABASE] Recebida mensagem de deleção de usuário via RabbitMQ. ID: " + id);
        try {
            usuarioPersistenceService.deleteById(id);
            System.out.println("[DATABASE] Usuário deletado com sucesso. ID: " + id);
        } catch (Exception e) {
            System.out.println("[DATABASE] Erro ao deletar usuário: " + e.getMessage());
            throw e;
        }
    }

}