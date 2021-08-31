package com.project.graalrestservice.web.exception_mapper;

import com.project.graalrestservice.domain.script.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

import static org.zalando.problem.Status.*;

/**
 * ZalandoExceptionHandler is needed to return JSON instead of a white page error
 */
@ControllerAdvice
public class ZalandoExceptionHandler implements ProblemHandling {

  @Override
  public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
    return Problem.builder().withType(type).withTitle(status.getReasonPhrase()).withStatus(status)
        .withType(type).with("message: ", throwable.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handlePageDoesNotExistException(PageDoesNotExistException e,
      NativeWebRequest request) {
    return create(NOT_FOUND, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleScriptNotFoundException(ScriptNotFoundException e,
      NativeWebRequest request) {
    return create(NOT_FOUND, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongArgumentException(WrongArgumentException e,
      NativeWebRequest request) {
    return create(BAD_REQUEST, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongNameException(WrongNameException e,
      NativeWebRequest request) {
    return create(CONFLICT, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongScriptException(WrongScriptException e,
      NativeWebRequest request) {
    return create(UNPROCESSABLE_ENTITY, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongScriptStatusException(WrongScriptStatusException e,
      NativeWebRequest request) {
    return create(FORBIDDEN, e, request);
  }

}
