package com.project.graalrestservice.exceptionHandling.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class PageDoesNotExistException extends AbstractThrowableProblem {

    public PageDoesNotExistException(int page) {
        super(null, "Not found", Status.NOT_FOUND, String.format("This page [%d] does not exist for the current list.", page));
    }
}
