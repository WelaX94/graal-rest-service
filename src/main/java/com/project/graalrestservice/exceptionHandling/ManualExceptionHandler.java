package com.project.graalrestservice.exceptionHandling;

import com.project.graalrestservice.domain.scriptHandler.exceptions.*;
import com.project.graalrestservice.representationModels.ExceptionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManualExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handlePageDoesNotExistException(PageDoesNotExistException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleScriptNotFoundException(ScriptNotFoundException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongArgumentException(WrongArgumentException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongNameException(WrongNameException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongScriptException(WrongScriptException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongScriptStatusException(WrongScriptStatusException e) {
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

}
