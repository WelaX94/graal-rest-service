package com.project.graalrestservice.domain.scriptHandler.exceptions; // NOSONAR

public class WrongScriptException extends RuntimeException {

  public WrongScriptException(String message) {
    super(message);
  }

}
