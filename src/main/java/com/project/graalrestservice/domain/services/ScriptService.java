package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;

public interface ScriptService {

    public String addScript(String name, String script, String link);

    public ScriptInfoForSingle getScriptInfo(String scriptName);

    public void stopScript(String scriptName);

    public void deleteScript(String scriptName);

    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page);

}
