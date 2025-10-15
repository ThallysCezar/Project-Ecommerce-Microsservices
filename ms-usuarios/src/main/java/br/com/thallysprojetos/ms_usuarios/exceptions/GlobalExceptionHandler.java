package br.com.thallysprojetos.ms_usuarios.exceptions;

import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorMessage> handleFeignNotFoundException(FeignException.NotFound ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado no serviço chamado via Feign: {}", ex.getMessage());
        String message = "Usuário não encontrado";
        return buildErrorResponse(new UsuarioNotFoundException(message), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ErrorMessage> handleFeignBadRequest(FeignException.BadRequest ex, HttpServletRequest request) {
        log.warn("Requisição inválida para serviço via Feign: {}", ex.getMessage());
        String message = "Requisição inválida ao buscar usuário";
        return buildErrorResponse(new IllegalArgumentException(message), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessage> handleFeignException(FeignException ex, HttpServletRequest request) {
        log.error("Erro ao comunicar com serviço via Feign: {}", ex.getMessage(), ex);
        String message = "Erro ao comunicar com o serviço de dados. Por favor, tente novamente.";
        HttpStatus status = HttpStatus.valueOf(ex.status());
        return buildErrorResponse(new Exception(message), request, status);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(UsuarioNotFoundException ex, HttpServletRequest request) {
        log.warn("Usuário não encontrado: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handleAlreadyExistException(UsuarioAlreadyExistException ex, HttpServletRequest request) {
        log.warn("Usuário já existe: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<ErrorMessage> handleUsuarioException(UsuarioException ex, HttpServletRequest request) {
        log.error("Erro ao processar usuário: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Erro de validação: {}", ex.getMessage());
        return buildValidationErrorResponse(ex, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Erro ao ler mensagem HTTP: {}", ex.getMessage());
        String message = "Corpo da requisição inválido ou malformado. Verifique o formato JSON.";
        return buildErrorResponse(new IllegalArgumentException(message), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Erro de tipo de argumento: {}", ex.getMessage());
        String message = String.format("O parâmetro '%s' deve ser do tipo %s", 
            ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido");
        return buildErrorResponse(new IllegalArgumentException(message), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argumento ilegal: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Erro não esperado: {}", ex.getMessage(), ex);
        String message = "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.";
        return buildErrorResponse(new Exception(message), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessage> buildErrorResponse(Exception ex, HttpServletRequest request, HttpStatus status) {
        ErrorMessage errorMessage = new ErrorMessage(request, status, ex.getMessage());
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessage);
    }

    private ResponseEntity<ErrorMessage> buildValidationErrorResponse(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(request, HttpStatus.BAD_REQUEST, 
            "Erro de validação nos campos", ex.getBindingResult());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessage);
    }

}