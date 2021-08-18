package com.project.graalrestservice.exceptionHandling.exceptions;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class WrongScriptStatusException extends AbstractThrowableProblem {

    public WrongScriptStatusException(String message, ScriptStatus scriptStatus) {
        super(null, "Forbidden", Status.FORBIDDEN, String.format("Wrong script status - %s. %s", scriptStatus, message));
    }

}
