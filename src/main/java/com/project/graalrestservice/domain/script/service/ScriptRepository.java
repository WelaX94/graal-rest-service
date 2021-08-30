package com.project.graalrestservice.domain.script.service;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import com.project.graalrestservice.domain.script.model.Script;

import java.util.List;

public interface ScriptRepository {

  void putScript(String scriptName, Script script);

  Script getScript(String scriptName);

  void deleteScript(String scriptName);

  List<Script> getScriptList(ScriptStatus status, String nameContains);

}
