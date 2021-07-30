package com.project.graalrestservice.services;

import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
import com.project.graalrestservice.repositories.ScriptList;
import com.project.graalrestservice.threads.ScriptExecutionThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class ScriptHandlerService implements ScriptHandler {

    @Autowired
    private ScriptList scriptList;

    @Autowired
    private ExecutorService executorService;

    public String addScript(String scriptName, String script) {

        ScriptInfo scriptInfo = new ScriptInfo(scriptName, script);
        if(!(checkName(scriptName)) || !scriptList.put(scriptName, scriptInfo)) throw new WrongNameException();

        ScriptExecutionThread scriptExecutionThread = new ScriptExecutionThread(scriptInfo);

        scriptInfo.setScriptExecutionThread(scriptExecutionThread);
        executorService.execute(scriptExecutionThread);

        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    @Override
    public String getAllScripts() {
        return scriptList.toString();
    }

    public String getScriptInfo(String scriptName) {
        ScriptInfo scriptInfo = scriptList.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        return "\t" + scriptName + "\n"
                + "Status: " + scriptInfo.getStatus() + "\n"
                + "Logs:\n\n"
                + scriptInfo.getLog();
    }

    private boolean checkName(String scriptName) {
        List<String> illegalNameSpace = new ArrayList<>();
        illegalNameSpace.add("help");
        for(String name: illegalNameSpace) {
            if(scriptName.equals(name)) return false;
        }
        return true;
    }

}
