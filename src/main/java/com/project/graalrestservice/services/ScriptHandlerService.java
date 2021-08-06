package com.project.graalrestservice.services;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
import com.project.graalrestservice.repositories.ScriptList;
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

    public String addScript(String scriptName, String script, String link) {
        checkName(scriptName);
        ScriptInfo scriptInfo = new ScriptInfo(script, link);
        scriptList.put(scriptName, scriptInfo);
        executorService.execute(scriptInfo);
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
                + scriptInfo.getOutputInfo();
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

    public ScriptHandlerService(ScriptList scriptList, ExecutorService executorService) {
        this.scriptList = scriptList;
        this.executorService = executorService;
    }
}
