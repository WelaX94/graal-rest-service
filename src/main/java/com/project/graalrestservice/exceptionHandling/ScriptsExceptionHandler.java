package com.project.graalrestservice.exceptionHandling;

import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ScriptsExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleWrongNameException(WrongNameException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleScriptNotFoundException(ScriptNotFoundException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongScriptStatusException(WrongScriptStatusException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
