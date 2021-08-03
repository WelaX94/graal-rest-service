package com.project.graalrestservice.exceptionHandling.exceptions;

public class WrongNameException extends RuntimeException{
    public WrongNameException(String message) {
        super("Wrong script name. " + message);
    }
}
