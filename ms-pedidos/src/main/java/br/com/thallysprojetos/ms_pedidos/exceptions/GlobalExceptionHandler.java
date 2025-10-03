package br.com.thallysprojetos.ms_pedidos.exceptions;

import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosAlreadyExistException;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosException;
import br.com.thallysprojetos.ms_pedidos.exceptions.pedidos.PedidosNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//    private final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({PedidosNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({PedidosAlreadyExistException.class})
    public ResponseEntity<ErrorMessage> handleAlreadyExistException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PedidosException.class})
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