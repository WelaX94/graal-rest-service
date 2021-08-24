package com.project.graalrestservice.domain.scriptHandler.services;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ScriptService {

  Script addScript(String name, String script);

  Script getScript(String scriptName);

  void stopScript(String scriptName);

  void deleteScript(String scriptName);

  List<Script> getScriptList(ScriptStatus scriptStatus, String nameContains,
      boolean orderByName, boolean reverseOrder);

  @Async
  void startScriptAsynchronously(Script script);

  void startScriptSynchronously(Script script);

}
