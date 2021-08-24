package com.project.graalrestservice.domain.scriptHandler.exceptions;

public class WrongScriptException extends RuntimeException {

    public WrongScriptException(String message) {
        super(message);
    }

}
