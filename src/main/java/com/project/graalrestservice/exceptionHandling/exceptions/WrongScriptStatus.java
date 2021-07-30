package com.project.graalrestservice.exceptionHandling.exceptions;

import com.project.graalrestservice.enums.ScriptStatus;

public class WrongScriptStatus extends RuntimeException{
    public WrongScriptStatus(String message, ScriptStatus scriptStatus) {
        super("Wrong script status - " + scriptStatus + ". " + message);
    }
}
