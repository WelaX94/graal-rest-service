package com.project.graalrestservice.domain.scriptHandler.exceptions; // NOSONAR

public class WrongArgumentException extends RuntimeException {

  public WrongArgumentException(String message) {
    super("Wrong argument. " + message);
  }

  public WrongArgumentException(char filter) {
    super("Unknown filter - " + filter);
  }

}
