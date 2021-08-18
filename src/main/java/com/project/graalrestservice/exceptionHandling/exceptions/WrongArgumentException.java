package com.project.graalrestservice.exceptionHandling.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class WrongArgumentException extends AbstractThrowableProblem {

    public WrongArgumentException(String message) {
        super(null, "Bad request", Status.BAD_REQUEST, "Wrong argument. " + message);
    }

    public WrongArgumentException(char filter) {
        super(null, "Bad request", Status.BAD_REQUEST, "Unknown filter - " + filter);
    }

}
