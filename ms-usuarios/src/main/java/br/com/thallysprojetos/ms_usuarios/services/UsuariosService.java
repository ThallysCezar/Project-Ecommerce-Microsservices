package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuariosService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public List<UsuariosDTO> findAll() {
        return databaseClient.findAll();
    }

    public UsuariosDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, UsuariosDTO.class))
                .orElseThrow(UsuarioNotFoundException::new);
    }

    public UsuariosDTO findByEmail(String email) {
        return databaseClient.findByEmail(email)
                .map(u -> modelMapper.map(u, UsuariosDTO.class))
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o e-mail fornecido."));
    }

    public UsuariosDTO createUser(UsuariosDTO dto) {
        System.out.println("[USUARIOS] Enviando mensagem de criação de usuário para RabbitMQ: " +
                "nome=" + dto.getUserName() +
                ", email=" + dto.getEmail() +
                ", id=" + dto.getId());
        try {
            rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.create", dto);
            System.out.println("[USUARIOS] Mensagem enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[USUARIOS] Erro ao enviar mensagem para RabbitMQ: " + e.getMessage());
            throw e;
        }
        return dto;
    }

    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
        UsuariosDTO existingUser = databaseClient.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));

        existingUser.setUserName(dto.getUserName());
        existingUser.setEmail(dto.getEmail());

        System.out.println("[USUARIOS] Enviando mensagem de atualização de usuário para RabbitMQ: " +
                "nome=" + existingUser.getUserName() +
                ", email=" + existingUser.getEmail() +
                ", id=" + existingUser.getId());
        try {
            rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.update", existingUser);
            System.out.println("[USUARIOS] Mensagem de atualização enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[USUARIOS] Erro ao enviar mensagem de atualização para RabbitMQ: " + e.getMessage());
            throw e;
        }

        return existingUser;
    }

    public void deleteUsuarios(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id);
        }

        System.out.println("[USUARIOS] Enviando mensagem de deleção de usuário para RabbitMQ. ID: " + id);

        try {
            rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.delete", id);
            System.out.println("[USUARIOS] Mensagem de deleção enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("[USUARIOS] Erro ao enviar mensagem de deleção para RabbitMQ: " + e.getMessage());
            throw e;
        }
    }

}