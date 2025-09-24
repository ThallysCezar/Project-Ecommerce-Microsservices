package br.com.thallysprojetos.ms_pagamentos.exceptions;

import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoAlreadyExistException;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoException;
import br.com.thallysprojetos.ms_pagamentos.exceptions.pagamento.PagamentoNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//    private final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({PagamentoNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({PagamentoAlreadyExistException.class})
    public ResponseEntity<ErrorMessage> handleAlreadyExistException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PagamentoException.class})
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessage> handleException(Exception ex, HttpServletRequest request, HttpStatus status) {
        String messageError = "Api Error: ";
//        LOGGER.error(messageError, ex);
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, status, ex.getMessage()));
    }

}