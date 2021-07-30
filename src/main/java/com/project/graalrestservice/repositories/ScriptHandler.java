package com.project.graalrestservice.repositories;

import com.project.graalrestservice.models.ScriptInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptHandler {

    public ScriptInfo addScript(String scriptName, String script);

}
