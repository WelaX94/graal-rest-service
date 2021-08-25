package com.project.graalrestservice.domain.scriptHandler.exceptions; // NOSONAR

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;

public class WrongScriptStatusException extends RuntimeException {

  public WrongScriptStatusException(String message, ScriptStatus scriptStatus) {
    super(String.format("Wrong script status - %s. %s", scriptStatus, message));
  }

}
