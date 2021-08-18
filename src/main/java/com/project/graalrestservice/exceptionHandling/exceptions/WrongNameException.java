package com.project.graalrestservice.exceptionHandling.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class WrongNameException extends AbstractThrowableProblem {

    public WrongNameException(String message) {
        super(null, "Conflict", Status.CONFLICT, "Wrong script name. " + message);
    }

}
