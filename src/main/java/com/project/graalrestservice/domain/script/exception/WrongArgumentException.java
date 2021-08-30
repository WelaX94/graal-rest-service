package com.project.graalrestservice.domain.script.exception;

public class WrongArgumentException extends RuntimeException {

  public WrongArgumentException(String message) {
    super("Wrong argument. " + message);
  }

  public WrongArgumentException(char filter) {
    super("Unknown filter - " + filter);
  }

}
