package com.project.graalrestservice.domain.script.exception;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;

public class WrongScriptStatusException extends RuntimeException {

  public WrongScriptStatusException(String message, ScriptStatus scriptStatus) {
    super(String.format("Wrong script status - %s. %s", scriptStatus, message));
  }

}
