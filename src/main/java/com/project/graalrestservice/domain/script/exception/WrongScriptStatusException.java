package com.project.graalrestservice.domain.script.exception;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;

import java.util.Arrays;

public class WrongScriptStatusException extends RuntimeException {

  public WrongScriptStatusException(String message, ScriptStatus currentStatus,
      ScriptStatus... allowedStatuses) {
    super("Wrong script status - " + currentStatus + ". " + message + ". Allowed statuses: "
        + Arrays.toString(allowedStatuses));
  }

}
