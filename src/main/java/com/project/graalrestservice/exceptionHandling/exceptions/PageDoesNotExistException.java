package com.project.graalrestservice.exceptionHandling.exceptions;

public class PageDoesNotExistException extends RuntimeException{
    public PageDoesNotExistException(int page) {
        super(String.format("This page [%d] does not exist for the current list.", page));
    }
}
