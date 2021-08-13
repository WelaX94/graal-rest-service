package com.project.graalrestservice.exceptionHandling.exceptions;

import com.project.graalrestservice.domain.enums.ScriptStatus;

public class WrongScriptStatusException extends RuntimeException{

    public WrongScriptStatusException(String message, ScriptStatus scriptStatus) {
        super("Wrong script status - " + scriptStatus + ". " + message);
    }

}
