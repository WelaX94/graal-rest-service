package com.project.graalrestservice.exceptionHandling.exceptions;

public class UnknownFilterException extends RuntimeException{
    public UnknownFilterException(String filter) {
        super("Unknown filter - " + filter);
    }
}
