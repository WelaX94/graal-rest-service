package com.project.graalrestservice.domain.scriptHandler.enums; // NOSONAR

import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongArgumentException;

/**
 * Enum script status flag class.
 */
public enum ScriptStatus {
  IN_QUEUE, RUNNING, EXECUTION_SUCCESSFUL, EXECUTION_FAILED, EXECUTION_CANCELED;

  /**
   * A method for converting textual status representation to enum.
   * 
   * @param status textual status
   * @return ScriptStatus. If the input is null, then the output is also null
   * @throws WrongArgumentException if the status could not be converted
   */
  public static ScriptStatus getStatus(String status) {
    if (status == null)
      return null;
    switch (status.toLowerCase()) {
      case "queue":
        return ScriptStatus.IN_QUEUE;
      case "running":
        return ScriptStatus.RUNNING;
      case "successful":
        return ScriptStatus.EXECUTION_SUCCESSFUL;
      case "failed":
        return ScriptStatus.EXECUTION_FAILED;
      case "canceled":
        return ScriptStatus.EXECUTION_CANCELED;
      default:
        throw new WrongArgumentException("Unknown status: " + status);
    }
  }

}
