package com.project.graalrestservice.exceptionHandling.exceptions;

public class WrongArgumentException extends RuntimeException{

    public final boolean listIsOver;

    public WrongArgumentException(String message) {
        super("Wrong argument. " + message);
        listIsOver = false;
    }

    public WrongArgumentException(int page) {
        super(String.format("This page [%d] does not exist for the current list.", page));
        listIsOver = true;
    }

    public WrongArgumentException(char filter) {
        super("Unknown filter - " + filter);
        listIsOver = false;
    }

}
