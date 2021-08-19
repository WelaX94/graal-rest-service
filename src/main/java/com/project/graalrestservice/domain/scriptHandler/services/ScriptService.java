package com.project.graalrestservice.domain.scriptHandler.services;

import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.representationModels.Page;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ScriptService {


    public Script addScript(String name, String script);

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public String getScriptLogs(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public Page<List<ScriptInfoForList>> getScriptListPage(String filters, int pageSize, int page);

    @Async
    public void startScriptAsynchronously(Script script);

    public void startScriptSynchronously(Script script);

}
