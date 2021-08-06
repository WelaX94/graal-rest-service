package com.project.graalrestservice.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface ScriptHandler {

    public String addScript(String name, String script, String link);

    public String getAllScripts();

    public String getScriptInfo(String scriptName);

    public String stopScript(String scriptName);

    public String deleteScript(String scriptName);

}
