package com.project.graalrestservice.domain.scriptHandler.exceptions; // NOSONAR

public class PageDoesNotExistException extends RuntimeException {

  public PageDoesNotExistException(int page) {
    super(String.format("This page [%d] does not exist for the current list.", page));
  }

}
