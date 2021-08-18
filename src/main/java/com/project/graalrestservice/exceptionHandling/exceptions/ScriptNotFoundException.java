package com.project.graalrestservice.exceptionHandling.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ScriptNotFoundException extends AbstractThrowableProblem {

    public ScriptNotFoundException(String scriptName) {
        super(null, "Not found", Status.NOT_FOUND, "Script '" + scriptName + "' not found");
    }

}
