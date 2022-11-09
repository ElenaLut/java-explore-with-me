package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        ApiError error = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> error.getErrors().add(er.toString()));
        error.setStatus(HttpStatus.NOT_FOUND.name());
        error.setMessage(e.getMessage());
        error.setReason("Not found");
        error.setTimestamp(Timestamp.from(Instant.now()));
        log.info("404: {}", e.getMessage());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final IncorrectRequestException e) {
        ApiError error = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> error.getErrors().add(er.toString()));
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setMessage(e.getMessage());
        error.setReason("Bad request");
        error.setTimestamp(Timestamp.from(Instant.now()));
        log.info("400: {}", e.getMessage());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        ApiError error = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> error.getErrors().add(er.toString()));
        error.setStatus(HttpStatus.FORBIDDEN.name());
        error.setMessage(e.getMessage());
        error.setReason("Forbidden");
        error.setTimestamp(Timestamp.from(Instant.now()));
        log.info("403: {}", e.getMessage());
        return error;
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(ConstraintViolationException ex) {
        log.info("400: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}