package com.project.graalrestservice.domain.scriptHandler.exceptions;

public class ScriptNotFoundException extends RuntimeException {

    public ScriptNotFoundException(String scriptName) {
        super("Script '" + scriptName + "' not found");
    }

}
