package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;

import java.util.List;
import java.util.Set;

public interface ScriptRepository {

    public void put(String scriptName, ScriptInfo scriptInfo);

    public ScriptInfo get(String scriptName);

    public void delete(String scriptName);

    public Set<ScriptInfoForList> getAllScripts(char ... filter);

    public List<ScriptInfoForList> getPageScripts(char[] filters, int page);

}
