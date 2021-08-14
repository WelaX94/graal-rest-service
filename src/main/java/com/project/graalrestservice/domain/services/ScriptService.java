package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;

public interface ScriptService {


    public ScriptInfo addScript(String name, String script, String logsLink, boolean readable);

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public String getScriptLogs(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page);

    public void startScriptAsynchronously(ScriptInfo scriptInfo);

    public void startScriptSynchronously(ScriptInfo scriptInfo);

}
