package org.account.mgmtsystem.resource;

import java.util.List;
import java.util.ArrayList;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;


@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUniqueIndexException(Exception ex) {
        return "Email has to be unique. Provided email already exists.";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<String> handleValidationExceptions(ConstraintViolationException ex) {
        var errors = new ArrayList<String>();
        ex.getConstraintViolations().forEach((error) -> {
            errors.add(error.getMessage());
        });
        return errors;
    }
}
