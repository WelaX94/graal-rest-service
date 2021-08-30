package com.project.graalrestservice.web.dto;

import org.springframework.http.HttpStatus;

/**
 * The class needed to return information to the user about the error in JSON format
 */
public class ExceptionInfo {
  private final String title;
  private final int status;
  private final String message;

  public ExceptionInfo(String message, HttpStatus httpStatus) {
    this.message = message;
    this.title = httpStatus.name();
    this.status = httpStatus.value();
  }

  public String getTitle() {
    return title;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

}
