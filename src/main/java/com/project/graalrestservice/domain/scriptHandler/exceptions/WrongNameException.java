package com.project.graalrestservice.domain.scriptHandler.exceptions; // NOSONAR

public class WrongNameException extends RuntimeException {

  public WrongNameException(String message) {
    super("Wrong script name. " + message);
  }

}
