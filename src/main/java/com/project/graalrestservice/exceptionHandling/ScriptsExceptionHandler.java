package com.project.graalrestservice.exceptionHandling;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.exceptionHandling.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ScriptsExceptionHandler {

    private final static Logger LOGGER = LogManager.getLogger(ScriptsExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<String> handleWrongNameException(WrongNameException exception) {
        LOGGER.info("Failed to process the request: " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleScriptNotFoundException(ScriptNotFoundException exception) {
        LOGGER.info("Failed to process the request: " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongScriptStatusException(WrongScriptStatusException exception) {
        LOGGER.info("Failed to process the request: " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongArgumentException(WrongArgumentException exception) {
        LOGGER.info("Failed to process the request: " + exception.getMessage());
        HttpStatus status;
        if (exception.listIsOver) status = HttpStatus.NOT_FOUND;
        else status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>(exception.getMessage(), status);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongScriptException(WrongScriptException exception) {
        LOGGER.info("Failed to process the request: " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
