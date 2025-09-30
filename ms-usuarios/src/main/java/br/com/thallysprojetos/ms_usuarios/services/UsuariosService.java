package br.com.thallysprojetos.ms_usuarios.services;

import br.com.thallysprojetos.ms_usuarios.configs.http.DatabaseClient;
import br.com.thallysprojetos.ms_usuarios.dtos.UsuariosDTO;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuariosService {

    private final DatabaseClient databaseClient;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public List<UsuariosDTO> findAll() { // <--- A assinatura muda para List
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

//    public UsuariosDTO createUser(UsuariosDTO dto) {
//        return databaseClient.createUsuarios(dto);
//    }

    public UsuariosDTO createUser(UsuariosDTO dto) {
        // Lógica de negócio: cria o usuário

        // Envia o comando para o ms-database via RabbitMQ (assíncrono)
        rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.create", dto);
        return dto;
    }

//    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
//        UsuariosDTO existingUser = databaseClient.findById(id)
//                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));
//
//        existingUser.setUserName(dto.getUserName());
//        existingUser.setEmail(dto.getEmail());
//        UsuariosDTO updatedUser = databaseClient.createUsuarios(existingUser);
//
//        return modelMapper.map(updatedUser, UsuariosDTO.class);
//    }

    public UsuariosDTO updateUsuarios(Long id, UsuariosDTO dto) {
        // 1. Busca o usuário (síncrono)
        UsuariosDTO existingUser = databaseClient.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));

        // 2. Aplica as alterações no DTO existente
        existingUser.setUserName(dto.getUserName());
        existingUser.setEmail(dto.getEmail());

        // 3. Envia o comando de atualização para o ms-database via RabbitMQ
        rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.update", existingUser);

        // Retorna o DTO atualizado (sem esperar o save no DB)
        return existingUser;
    }

//    public void deleteUsuarios(Long id) {
//        if (!databaseClient.existsById(id)) {
//            throw new UsuarioNotFoundException(String.format("Usuarios não encontrado com o id '%s'.", id));
//        }
//        databaseClient.deleteUsuarios(id);
//    }

    public void deleteUsuarios(Long id) {
        if (!databaseClient.existsById(id)) {
            throw new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        // Envia o comando de exclusão para o ms-database via RabbitMQ
        rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.delete", id);
    }

}