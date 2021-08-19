package com.project.graalrestservice.domain.scriptHandler.services.serviceImplementations;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptRepository;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongNameException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A class for processing commands over scripts
 */
@Service
public class ScriptServiceImpl implements ScriptService {

    private final ScriptRepository scriptRepository;
    private final int streamCapacity;
    private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
    private final String[] illegalNamespace = new String[]{"swagger-ui"};

    @Autowired
    public ScriptServiceImpl(
            ScriptRepository scriptRepository,
            @org.springframework.beans.factory.annotation.Value("${scripts.outputStream.capacity}") int streamCapacity) {
        this.scriptRepository = scriptRepository;
        this.streamCapacity = streamCapacity;
    }

    /**
     * A method to get a script page with specified parameters
     * @param filters filter list
     * @param pageSize page size
     * @param page page
     * @return Page with specified parameters
     */
    @Override
    public Page<List<ScriptInfoForList>> getScriptListPage(String filters, int pageSize, int page) {
        return scriptRepository.getScriptListPage(filters, pageSize, page);
    }

    /**
     * A method for checking a script for validity and adding it to the list of scripts
     * @param scriptName script name (identifier)
     * @param scriptCode JS script
     * @param logsLink link to script output logs page
     * @return Script with information about the script
     * @throws WrongScriptException if script has syntax error
     */
    @Override
    public Script addScript(String scriptName, String scriptCode) {
        checkName(scriptName);
        Script script = Script.create(scriptName, scriptCode, streamCapacity);
        scriptRepository.put(scriptName, script);
        return script;
    }

    /**
     * Method for running the script in asynchronous mode
     * @param script Script of the script to run
     */
    @Override
    public void startScriptAsynchronously(Script script) {
        script.run();
    }

    /**
     * Method for running the script in synchronous mode
     * @param script Script of the script to run
     */
    @Override
    public void startScriptSynchronously(Script script) {
        script.run();
    }

    /**
     * A method to get information about the script you are looking for
     * @param scriptName script name (identifier)
     * @return ScriptInfoForSingle with information for a single display
     */
    @Override
    public ScriptInfoForSingle getScriptInfo(String scriptName) {
        return new ScriptInfoForSingle(scriptRepository.get(scriptName));
    }

    /**
     * A method for retrieving the script logs
     * @param scriptName script name (identifier)
     * @return String with script logs
     */
    @Override
    public String getScriptLogs(String scriptName) {
        return scriptRepository.get(scriptName).getOutputLogs();
    }

    /**
     * Method for stopping a running script
     * @param scriptName script name (identifier)
     */
    @Override
    public void stopScript(String scriptName) {
        scriptRepository.get(scriptName).stopScriptExecution();
    }

    /**
     * Method for removing a script from the list
     * @param scriptName script name (identifier)
     * @throws WrongScriptException if script is running
     */
    @Override
    public void deleteScript(String scriptName) {
        final Script script = scriptRepository.get(scriptName);
        synchronized (script) {
            if (script.getScriptStatus() == ScriptStatus.RUNNING) throw new WrongScriptStatusException
                    ("To delete a running script, you must first stop it", script.getScriptStatus());
            script.closeContext();
            scriptRepository.delete(scriptName);
        }
    }

    /**
     * A method for checking a script name for forbidden words and characters
     * @param scriptName script name (identifier)
     * @throws WrongNameException if script name contains forbidden words and symbols
     */
    private void checkName(String scriptName) {
        if (!correctlyScriptName.matcher(scriptName).matches())
            throw new WrongNameException("The name uses illegal characters or exceeds the allowed length");
        for(String name: illegalNamespace) {
            if (name.equals(scriptName))
                throw new WrongNameException("This name is reserved and is forbidden for use");
        }
    }

}
