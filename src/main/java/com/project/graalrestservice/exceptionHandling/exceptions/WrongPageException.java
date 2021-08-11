package com.project.graalrestservice.exceptionHandling.exceptions;

public class WrongPageException extends RuntimeException{
    public final boolean listIsOver;

    public WrongPageException(String message) {
        super("The page number is entered incorrectly. " + message);
        listIsOver = false;
    }

    public WrongPageException(int page) {
        super(String.format("This page [%d] does not exist for the current list.", page));
        listIsOver = true;
    }
}
