package com.project.graalrestservice.exceptionHandling;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

@ControllerAdvice
public class ZalandoExceptionHandler implements ProblemHandling {

    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
        return Problem.builder()
                .withType(type)
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withType(type).with("message: ", throwable.getMessage());
    }

}