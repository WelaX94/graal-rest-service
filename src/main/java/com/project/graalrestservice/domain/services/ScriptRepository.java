package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;

import java.util.Set;

public interface ScriptRepository {

    public void put(String scriptName, ScriptInfo scriptInfo);

    public ScriptInfo get(String scriptName);

    public void delete(String scriptName);

    public String toString();

    public Set<ScriptInfoForList> getAll();

}
