package com.challenge.rental_cars_spring_api.core.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleIOException(BusinessException e) throws IOException {
        ApiError erro = new ApiError(e.getMessage(), e.getErrorCode().codigo());
        logger.severe(erro.toString());
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handlerMaxSizeException(MaxUploadSizeExceededException e) throws IOException {
        ApiErrorCode errorCode = ApiErrorCode.MAX_FILE_SIZE_EXCEED;
        ApiError erro = new ApiError(errorCode.codigo(),  errorCode.mensagem());
        logger.severe(erro.toString());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(erro);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handlerNotFound(NoHandlerFoundException ex) {
        ApiErrorCode errorCode = ApiErrorCode.RESOURCE_NOT_FOUND;
        ApiError erro = new ApiError(errorCode.codigo(), errorCode.mensagem());
        logger.severe(erro.toString());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiErrorCode errorCode = ApiErrorCode.UNEXPECTED_ERROR;
        ApiError erro = new ApiError(errorCode.codigo(), errorCode.mensagem());
        logger.severe(erro.toString());
        return ResponseEntity.internalServerError().body(erro);
    }
}
