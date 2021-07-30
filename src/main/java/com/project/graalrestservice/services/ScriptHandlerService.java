package com.project.graalrestservice.services;

import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
import com.project.graalrestservice.repositories.ScriptList;
import com.project.graalrestservice.threads.ScriptExecutionThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class ScriptHandlerService implements ScriptHandler {

    @Autowired
    private ScriptList scriptList;

    @Autowired
    private ExecutorService executorService;

    public ScriptInfo addScript(String scriptName, String script) {

        ScriptInfo scriptInfo = new ScriptInfo(scriptName, script);
        if(!(checkName(scriptName)) || !scriptList.put(scriptName, scriptInfo)) throw new WrongNameException();

        ScriptExecutionThread scriptExecutionThread = new ScriptExecutionThread(scriptInfo);

        scriptInfo.setScriptExecutionThread(scriptExecutionThread);
        executorService.execute(scriptExecutionThread);

        return scriptInfo;
    }

    private boolean checkName(String scriptName) {
        return !(scriptName.equals("info"));
    }

}
