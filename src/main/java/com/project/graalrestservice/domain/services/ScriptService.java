package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;

import java.util.Set;

public interface ScriptService {

    public String addScript(String name, String script, String link);

    public String getAllScripts();

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public Set<ScriptInfoForList> getAll();

}
