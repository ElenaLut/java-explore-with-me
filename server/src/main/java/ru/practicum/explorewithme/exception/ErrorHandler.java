package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        String objectName = e.getObjectName();
        int errorCount = e.getErrorCount();
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason(String.format("During [%s] validation found %s errors", objectName, errorCount))
                .errors(getErrors(e.getFieldErrors()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(ConstraintViolationException ex) {
        log.info("400: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private List<String> getErrors(List<FieldError> fieldErrors) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add("Field: " + fieldError.getField() +
                    ". Error: " + fieldError.getDefaultMessage() +
                    ". Value: " + fieldError.getRejectedValue());
        }
        return errors;
    }
}