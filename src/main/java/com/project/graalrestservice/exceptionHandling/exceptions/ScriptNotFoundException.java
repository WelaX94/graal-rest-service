package com.project.graalrestservice.exceptionHandling.exceptions;

public class ScriptNotFoundException extends RuntimeException{
    public ScriptNotFoundException(String scriptName) {
        super("Script '" + scriptName + "' not found");
    }
}
