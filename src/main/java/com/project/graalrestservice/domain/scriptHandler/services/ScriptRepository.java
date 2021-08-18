package com.project.graalrestservice.domain.scriptHandler.services;

import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;

import java.util.List;

public interface ScriptRepository {

    public void put(String scriptName, Script script);

    public Script get(String scriptName);

    public void delete(String scriptName);

    public Page<List<ScriptInfoForList>> getScriptListPage(String filters, int pageSize, int page);

}
