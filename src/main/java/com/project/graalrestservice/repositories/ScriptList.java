package com.project.graalrestservice.repositories;

import com.project.graalrestservice.models.ScriptInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptList {

    public void put(String scriptName, ScriptInfo scriptInfo);

    public ScriptInfo get(String scriptName);

    public void delete(String scriptName);

    public String toString();

}
