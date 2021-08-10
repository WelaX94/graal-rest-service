package com.project.graalrestservice.exceptionHandling.exceptions;

public class WrongScriptException extends RuntimeException{
    public WrongScriptException (String message) {
        super("Script parsing error. " + message);
    }
}
