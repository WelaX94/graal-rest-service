package com.project.graalrestservice.domain.scriptHandler.services;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ScriptService {


    public Script addScript(String name, String script);

    public Script getScript(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public List<Script> getScriptList(ScriptStatus scriptStatus, String nameContains, boolean orderByName, boolean reverseOrder);

    @Async
    public void startScriptAsynchronously(Script script);

    public void startScriptSynchronously(Script script);

}
