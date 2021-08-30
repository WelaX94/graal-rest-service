package com.project.graalrestservice.domain.script.exception;

public class WrongNameException extends RuntimeException {

  public WrongNameException(String message) {
    super("Wrong script name. " + message);
  }

}
