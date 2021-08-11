package com.project.graalrestservice.exceptionHandling;

import com.project.graalrestservice.exceptionHandling.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ScriptsExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleWrongNameException(WrongNameException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleScriptNotFoundException(ScriptNotFoundException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongScriptStatusException(WrongScriptStatusException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnknownFilterException(UnknownFilterException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongPageException(WrongPageException exception) {
        HttpStatus status;
        if (exception.listIsOver) status = HttpStatus.NOT_FOUND;
        else status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>(exception.getMessage(), status);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongScriptException(WrongScriptException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
