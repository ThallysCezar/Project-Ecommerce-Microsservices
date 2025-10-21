package br.com.thallysprojetos.api_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Erro capturado pelo GlobalExceptionHandler: {}", ex.getMessage(), ex);

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Erro interno do servidor";

        // Customiza status e mensagem baseado no tipo de exceção
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Token JWT")) {
                status = HttpStatus.UNAUTHORIZED;
                message = ex.getMessage();
            } else if (ex.getMessage().contains("403") || ex.getMessage().contains("Forbidden")) {
                status = HttpStatus.FORBIDDEN;
                message = "Acesso negado";
            } else if (ex.getMessage().contains("404") || ex.getMessage().contains("Not Found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Recurso não encontrado";
            } else if (ex.getMessage().contains("500")) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Erro no serviço downstream";
            }
        }

        exchange.getResponse().setStatusCode(status);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getPath().value());

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar resposta de erro", e);
            bytes = "{\"error\":\"Erro ao processar resposta\"}".getBytes();
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
