package com.project.graalrestservice.domain.script.exception;

public class WrongArgumentException extends RuntimeException {

  public WrongArgumentException(String message) {
    super("Wrong argument. " + message);
  }

  public WrongArgumentException(String message, Exception exception) {
    super("Wrong argument. " + message, exception);
  }

}
