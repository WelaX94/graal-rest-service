package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.services.ScriptHandler;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

@Service
public class ScriptHandlerImpl implements ScriptHandler {

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private ExecutorService executorService;

    public String addScript(String scriptName, String script, String link) {
        checkName(scriptName);
        ScriptInfo scriptInfo = new ScriptInfo(script, link);
        scriptService.put(scriptName, scriptInfo);
        executorService.execute(scriptInfo);
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    @Override
    public String getAllScripts() {
        return scriptService.toString();
    }

    @Override
    public String getScriptInfo(String scriptName) {
        ScriptInfo scriptInfo = scriptService.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        return "Script: " + scriptName + "\n"
                + "Status: " + scriptInfo.getScriptStatus() + "\n"
                + "Logs:\n\n"
                + scriptInfo.getLogStream()
                + scriptInfo.getOutputInfo();
    }

    @Override
    public String stopScript(String scriptName) {
        ScriptInfo scriptInfo = scriptService.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        if (scriptInfo.getScriptStatus() != ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("You cannot stop a script that is not running", scriptInfo.getScriptStatus());
        scriptInfo.getContext().close(true);
        return "Script '" + scriptName + "' stopped";
    }

    @Override
    public String deleteScript(String scriptName) {
        ScriptInfo scriptInfo = scriptService.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        if (scriptInfo.getScriptStatus() == ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("To delete a running script, you must first stop it", scriptInfo.getScriptStatus());
        scriptService.delete(scriptName);
        return "Script '" + scriptName + "' deleted";
    }

    private void checkName(String scriptName) {
        if (!(scriptService.get(scriptName) == null)) throw new WrongNameException("Such a name is already in use");
        Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
        boolean checkName = correctlyScriptName.matcher(scriptName).matches();
        if (!checkName) throw new WrongNameException("The name uses illegal characters or exceeds the allowed length");
    }

    public ScriptHandlerImpl() {
    }

    public ScriptHandlerImpl(ScriptService scriptService, ExecutorService executorService) {
        this.scriptService = scriptService;
        this.executorService = executorService;
    }
}
