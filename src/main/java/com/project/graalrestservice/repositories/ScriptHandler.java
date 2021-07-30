package com.project.graalrestservice.repositories;

import com.project.graalrestservice.models.ScriptInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptHandler {

    public String addScript(String scriptName, String script);

    public String getAllScripts();

    public String getScriptInfo(String scriptName);

}
