package com.project.graalrestservice.domain.services;

import com.project.graalrestservice.domain.models.ScriptInfo;

public interface ScriptService {

    public void put(String scriptName, ScriptInfo scriptInfo);

    public ScriptInfo get(String scriptName);

    public void delete(String scriptName);

    public String toString();

}