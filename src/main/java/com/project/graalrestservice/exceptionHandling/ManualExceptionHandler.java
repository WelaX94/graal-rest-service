package com.project.graalrestservice.exceptionHandling;

import com.project.graalrestservice.domain.scriptHandler.exceptions.*;
import com.project.graalrestservice.representationModels.ExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManualExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ManualExceptionHandler.class);
    private static final String mdcNameIdentifier = "scriptName";

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handlePageDoesNotExistException(PageDoesNotExistException e) {
        logger.info("[{}] - Page doesn't exist. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleScriptNotFoundException(ScriptNotFoundException e) {
        logger.info("[{}] - Script not found. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongArgumentException(WrongArgumentException e) {
        logger.info("[{}] - Wrong argument. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongNameException(WrongNameException e) {
        logger.info("[{}] - Wrong name. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongScriptException(WrongScriptException e) {
        logger.info("[{}] - Wrong script. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleWrongScriptStatusException(WrongScriptStatusException e) {
        logger.info("[{}] - Wrong script status. Exception processed successfully.", MDC.get(mdcNameIdentifier));
        return new ResponseEntity<>(new ExceptionInfo(e.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

}
