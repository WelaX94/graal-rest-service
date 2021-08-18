package com.project.graalrestservice.exceptionHandling.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class WrongScriptException extends AbstractThrowableProblem {

    public WrongScriptException (String message) {
        super(null, "Unprocessable entity", Status.UNPROCESSABLE_ENTITY, message);
    }

}
