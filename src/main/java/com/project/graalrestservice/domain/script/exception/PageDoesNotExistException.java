package com.project.graalrestservice.domain.script.exception;

public class PageDoesNotExistException extends RuntimeException {

  public PageDoesNotExistException(int page) {
    super(String.format("This page [%d] does not exist for the current list.", page));
  }

}
