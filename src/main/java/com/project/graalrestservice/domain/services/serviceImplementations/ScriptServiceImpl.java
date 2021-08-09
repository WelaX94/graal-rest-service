package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ExecutorService executorService;

    private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");

    @Override
    public Set<ScriptInfoForList> getAllScripts() {
        return scriptRepository.getAllScripts();
    }

    @Override
    public String addScript(String scriptName, String script, String link) {
        checkName(scriptName);
        final ScriptInfo scriptInfo = new ScriptInfo(script, link);
        scriptRepository.put(scriptName, scriptInfo);
        executorService.execute(scriptInfo);
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    @Override
    public ScriptInfoForSingle getScriptInfo(String scriptName) {
        final ScriptInfo scriptInfo = scriptRepository.get(scriptName);
        return new ScriptInfoForSingle(scriptName, scriptInfo);
    }

    @Override
    public void stopScript(String scriptName) {
        scriptRepository.get(scriptName).stopScriptExecution();
    }

    @Override
    public void deleteScript(String scriptName) {
        final ScriptInfo scriptInfo = scriptRepository.get(scriptName);
        if (scriptInfo.getScriptStatus() == ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("To delete a running script, you must first stop it", scriptInfo.getScriptStatus());
        scriptRepository.delete(scriptName);
    }

    private void checkName(String scriptName) {
        if (!correctlyScriptName.matcher(scriptName).matches())
            throw new WrongNameException("The name uses illegal characters or exceeds the allowed length");
    }

    public ScriptServiceImpl() {
    }

    public ScriptServiceImpl(ScriptRepository scriptRepository, ExecutorService executorService) {
        this.scriptRepository = scriptRepository;
        this.executorService = executorService;
    }

}
