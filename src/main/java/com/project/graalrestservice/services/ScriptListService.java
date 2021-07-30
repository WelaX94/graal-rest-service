package com.project.graalrestservice.services;

import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptList;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptListService implements ScriptList {

    ConcurrentHashMap<String, ScriptInfo> list = new ConcurrentHashMap<>();

    @Override
    public void update(String scriptName, ScriptInfo scriptInfo) {
        list.put(scriptName, scriptInfo);
    }

    @Override
    public boolean put(String scriptName, ScriptInfo scriptInfo) {
        if(list.putIfAbsent(scriptName, scriptInfo) == null) return true;
        else return false;
    }

    @Override
    public ScriptInfo get(String scriptName) {
        return list.get(scriptName);
    }

    public void delete(String scriptName) {
        list.remove(scriptName);
    }

    @Override
    public String toString() {
        String result = "";
        for(Map.Entry<String, ScriptInfo> entry: list.entrySet()) {
            result +=
                    entry.getKey() + ":\n"
                    + "\tStatus: " + entry.getValue().getStatus() + "\n"
                    + "\tLink: " + entry.getValue().getLink() + "\n\n";
        }
        return result;
    }

}
