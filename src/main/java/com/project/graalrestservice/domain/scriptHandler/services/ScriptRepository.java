package com.project.graalrestservice.domain.scriptHandler.services; // NOSONAR

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;

import java.util.List;

public interface ScriptRepository {

  void putScript(String scriptName, Script script);

  Script getScript(String scriptName);

  void deleteScript(String scriptName);

  List<Script> getScriptList(ScriptStatus status, String nameContains);

}
