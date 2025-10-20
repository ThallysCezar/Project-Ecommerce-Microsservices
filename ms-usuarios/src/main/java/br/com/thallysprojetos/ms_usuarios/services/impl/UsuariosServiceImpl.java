package br.com.thallysprojetos.ms_usuarios.services.impl;

import br.com.thallysprojetos.common_dtos.usuario.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import br.com.thallysprojetos.ms_usuarios.services.UsuariosService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UsuariosServiceImpl implements UsuariosService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    @Override
    public List<UsuariosDTO> findAll() {
        return databaseClient.findAll();
    }

    @Override
    public UsuariosDTO findById(Long id) {
        return databaseClient.findById(id)
                .map(p -> modelMapper.map(p, UsuariosDTO.class))
                .orElseThrow(UsuarioNotFoundException::new);
    }

    @Override
    public UsuariosDTO findByEmail(String email) {
        return databaseClient.findByEmail(email)
                .map(u -> modelMapper.map(u, UsuariosDTO.class))
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o e-mail fornecido."));
    }

    @Override
    public UsuariosDTO createUser(UsuariosDTO dto) {
        verificationExistingUser(dto.getId(), dto);

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

    @Override
    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
        UsuariosDTO existingUser = databaseClient.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));

        if (!existingUser.getEmail().equals(dto.getEmail())) {
            verificationExistingUser(id, dto);
        }

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

    @Override
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

    private void verificationExistingUser(Long id, UsuariosDTO dto) {
        try {
            Optional<UsuariosDTO> userWithEmail = databaseClient.findByEmail(dto.getEmail());
            if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
                throw new UsuarioAlreadyExistException(
                        "O email " + dto.getEmail() + " já está sendo usado por outro usuário"
                );
            }
        } catch (FeignException.NotFound e) {
            log.info("Email {} está disponível. Validação de duplicidade passou com sucesso.", dto.getEmail());
        }
    }

}