package com.project.graalrestservice.exceptionHandling; // NOSONAR

import com.project.graalrestservice.domain.scriptHandler.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

/**
 * ZalandoExceptionHandler is needed to return JSON instead of a white page error
 */
@ControllerAdvice
public class ZalandoExceptionHandler implements ProblemHandling {

  private static final Logger logger = LoggerFactory.getLogger(ZalandoExceptionHandler.class);
  private static final String MDC_NAME_IDENTIFIER = "scriptName";

  @Override
  public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
    return Problem.builder().withType(type).withTitle(status.getReasonPhrase()).withStatus(status)
        .withType(type).with("message: ", throwable.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handlePageDoesNotExistException(PageDoesNotExistException e,
      NativeWebRequest request) {
    logger.info("[{}] - Page doesn't exist. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.NOT_FOUND, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleScriptNotFoundException(ScriptNotFoundException e,
      NativeWebRequest request) {
    logger.info("[{}] - Script not found. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.NOT_FOUND, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongArgumentException(WrongArgumentException e,
      NativeWebRequest request) {
    logger.info("[{}] - Wrong argument. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.BAD_REQUEST, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongNameException(WrongNameException e,
      NativeWebRequest request) {
    logger.info("[{}] - Wrong name. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.CONFLICT, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongScriptException(WrongScriptException e,
      NativeWebRequest request) {
    logger.info("[{}] - Wrong script. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.UNPROCESSABLE_ENTITY, e, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleWrongScriptStatusException(WrongScriptStatusException e,
      NativeWebRequest request) {
    logger.info("[{}] - Wrong script status. Exception processed successfully.",
        MDC.get(MDC_NAME_IDENTIFIER));
    return create(Status.FORBIDDEN, e, request);
  }

}
