package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.services.ScriptRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptRepositoryImpl implements ScriptRepository {

    ConcurrentHashMap<String, ScriptInfo> map = new ConcurrentHashMap<>();

    @Override
    public void put(String scriptName, ScriptInfo scriptInfo) {
        map.put(scriptName, scriptInfo);
    }

    @Override
    public ScriptInfo get(String scriptName) {
        return map.get(scriptName);
    }

    public Set<ScriptInfoForList> getAll() {
        Set<ScriptInfoForList> set = new TreeSet<>();
        for(Map.Entry<String, ScriptInfo> entry: map.entrySet()) {
            set.add(new ScriptInfoForList(entry.getKey(), entry.getValue()));
        }
        return set;
    }

    public void delete(String scriptName) {
        map.remove(scriptName);
    }

    @Override
    public String toString() {
        String result = "";
        for(Map.Entry<String, ScriptInfo> entry: map.entrySet()) {
            result +=
                    entry.getKey() + ":\n"
                    + "\tStatus: " + entry.getValue().getScriptStatus() + "\n"
                    + "\tLink: " + entry.getValue().getLink() + "\n\n";
        }
        return result;
    }

    public ScriptRepositoryImpl() {
    }

    public ScriptRepositoryImpl(ConcurrentHashMap<String, ScriptInfo> map) {
        this.map = map;
    }
}
