package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import org.springframework.scheduling.annotation.Async;

public interface ScriptService {


    public ScriptInfo addScript(String name, String script, String link);

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page);

    @Async
    public String startScriptAsynchronously(ScriptInfo scriptInfo);

    public String startScriptSynchronously(ScriptInfo scriptInfo);

}
