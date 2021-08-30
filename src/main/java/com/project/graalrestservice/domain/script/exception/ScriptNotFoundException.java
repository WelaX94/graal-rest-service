package com.project.graalrestservice.domain.script.exception;

public class ScriptNotFoundException extends RuntimeException {

  public ScriptNotFoundException(String scriptName) {
    super("Script '" + scriptName + "' not found");
  }

}
