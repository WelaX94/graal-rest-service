package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptRepositoryImpl implements ScriptRepository {

    ConcurrentHashMap<String, ScriptInfo> map = new ConcurrentHashMap<>();

    @Override
    public void put(String scriptName, ScriptInfo scriptInfo) {
        if (map.putIfAbsent(scriptName, scriptInfo) != null) throw new WrongNameException("Such a name is already in use");
    }

    @Override
    public ScriptInfo get(String scriptName) {
        ScriptInfo scriptInfo = map.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        return scriptInfo;
    }

    public Set<ScriptInfoForList> getAllScripts() {
        Set<ScriptInfoForList> set = new TreeSet<>();
        for(Map.Entry<String, ScriptInfo> entry: map.entrySet()) {
            set.add(new ScriptInfoForList(entry.getKey(), entry.getValue()));
        }
        return set;
    }

    public void delete(String scriptName) {
        if(map.remove(scriptName) == null) throw new ScriptNotFoundException(scriptName);
    }

    public ScriptRepositoryImpl() {
    }

    public ScriptRepositoryImpl(ConcurrentHashMap<String, ScriptInfo> map) {
        this.map = map;
    }
}
