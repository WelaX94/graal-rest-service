package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.representationModels.ScriptListPage;

public interface ScriptRepository {

    public void put(String scriptName, ScriptInfo scriptInfo);

    public ScriptInfo get(String scriptName);

    public void delete(String scriptName);

    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page);

}
