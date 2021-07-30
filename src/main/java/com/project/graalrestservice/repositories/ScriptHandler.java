package com.project.graalrestservice.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface ScriptHandler {

    public String addScript(String scriptName, String script);

    public String getAllScripts();

    public String getScriptInfo(String scriptName);

    public String stopScript(String scriptName);

}
