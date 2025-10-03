package br.com.thallysprojetos.ms_usuarios.exceptions;

import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioAlreadyExistException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioException;
import br.com.thallysprojetos.ms_usuarios.exceptions.usuarios.UsuarioNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//    private final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({UsuarioNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({UsuarioAlreadyExistException.class})
    public ResponseEntity<ErrorMessage> handleAlreadyExistException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UsuarioException.class})
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessage> handleException(Exception ex, HttpServletRequest request, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, status, ex.getMessage()));
    }

}