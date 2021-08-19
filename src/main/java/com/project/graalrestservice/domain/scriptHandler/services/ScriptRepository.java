package com.project.graalrestservice.domain.scriptHandler.services;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;

import java.util.List;

public interface ScriptRepository {

    public void putScript(String scriptName, Script script);

    public Script getScript(String scriptName);

    public void deleteScript(String scriptName);

    public List<Script> getScriptList(ScriptStatus status, String nameContains);

}
