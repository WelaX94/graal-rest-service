package com.project.graalrestservice.services;

import com.project.graalrestservice.applicationLogic.enums.ScriptStatus;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.applicationLogic.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
import com.project.graalrestservice.repositories.ScriptList;
import com.project.graalrestservice.applicationLogic.threads.ScriptExecutionThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

@Service
public class ScriptHandlerService implements ScriptHandler {

    @Autowired
    private ScriptList scriptList;

    @Autowired
    private ExecutorService executorService;

    @Value("${application.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    public String addScript(String scriptName, String script) {
        checkName(scriptName);
        ScriptInfo scriptInfo = new ScriptInfo(scriptName, script, host, port);
        scriptList.put(scriptName, scriptInfo);
        ScriptExecutionThread scriptExecutionThread = new ScriptExecutionThread(scriptInfo);
        executorService.execute(scriptExecutionThread);
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    @Override
    public String getAllScripts() {
        return scriptList.toString();
    }

    @Override
    public String getScriptInfo(String scriptName) {
        ScriptInfo scriptInfo = scriptList.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        return "Script: " + scriptName + "\n"
                + "Status: " + scriptInfo.getScriptStatus() + "\n"
                + "Logs:\n\n"
                + scriptInfo.getLogStream()
                + scriptInfo.getError();
    }

    @Override
    public String stopScript(String scriptName) {
        ScriptInfo scriptInfo = scriptList.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        if (scriptInfo.getScriptStatus() != ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("You cannot stop a script that is not running", scriptInfo.getScriptStatus());
        scriptInfo.getContext().close(true);
        return "Script '" + scriptName + "' stopped";
    }

    @Override
    public String deleteScript(String scriptName) {
        ScriptInfo scriptInfo = scriptList.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        if (scriptInfo.getScriptStatus() == ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("To delete a running script, you must first stop it", scriptInfo.getScriptStatus());
        scriptList.delete(scriptName);
        return "Script '" + scriptName + "' deleted";
    }

    private void checkName(String scriptName) {
        if (!(scriptList.get(scriptName) == null)) throw new WrongNameException("Such a name is already in use");
        Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
        boolean checkName = correctlyScriptName.matcher(scriptName).matches();
        if (!checkName) throw new WrongNameException("The name uses illegal characters or exceeds the allowed length");
    }

    public ScriptHandlerService() {
    }

    public ScriptHandlerService(ScriptList scriptList, String host, int port, ExecutorService executorService) {
        this.scriptList = scriptList;
        this.host = host;
        this.port = port;
        this.executorService = executorService;
    }
}
