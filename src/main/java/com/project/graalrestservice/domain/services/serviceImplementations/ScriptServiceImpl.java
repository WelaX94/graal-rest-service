package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.services.ScriptService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptServiceImpl implements ScriptService {

    ConcurrentHashMap<String, ScriptInfo> list = new ConcurrentHashMap<>();

    @Override
    public void put(String scriptName, ScriptInfo scriptInfo) {
        list.put(scriptName, scriptInfo);
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
                    + "\tStatus: " + entry.getValue().getScriptStatus() + "\n"
                    + "\tLink: " + entry.getValue().getLink() + "\n\n";
        }
        return result;
    }

    public ScriptServiceImpl() {
    }

    public ScriptServiceImpl(ConcurrentHashMap<String, ScriptInfo> list) {
        this.list = list;
    }
}
