package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.representationModels.Page;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ScriptService {


    public ScriptInfo addScript(String name, String script, String logsLink, boolean readable);

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public String getScriptLogs(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public Page<List<ScriptInfoForList>> getScriptListPage(String filters, int pageSize, int page);

    @Async
    public void startScriptAsynchronously(ScriptInfo scriptInfo);

    public void startScriptSynchronously(ScriptInfo scriptInfo);

}
