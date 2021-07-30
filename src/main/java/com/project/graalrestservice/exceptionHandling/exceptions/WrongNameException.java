package com.project.graalrestservice.exceptionHandling.exceptions;

public class WrongNameException extends RuntimeException{
    public WrongNameException() {
        super("The script name is not entered correctly, or the name is already in use");
    }
}
