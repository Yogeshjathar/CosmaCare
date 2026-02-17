package com.cosmacare.cosmacare_user_service.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundExceptions.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundExceptions ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<String> generalExceptionHandler(Exception ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
   }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnum(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            if (ife.getTargetType().isEnum()) {

                ValidationErrorResponse error = new ValidationErrorResponse(
                        "INVALID_ENUM",
                        "Invalid value '" + ife.getValue() + "' for enum " + ife.getTargetType().getSimpleName(),
                        ife.getValue(),
                        Stream.of(ife.getTargetType().getEnumConstants()).map(Object::toString).toList(),
                        request.getRequestURI(),
                        HttpStatus.BAD_REQUEST.value()
                );
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        // fallback for other malformed JSON
        ErrorResponse error = new ErrorResponse("INVALID_REQUEST", "Malformed request body", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
