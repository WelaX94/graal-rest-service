package com.project.graalrestservice.domain.scriptHandler.services.serviceImplementations;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptRepository;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongNameException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class for processing commands over scripts
 */
@Service
public class ScriptServiceImpl implements ScriptService {

    private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);
    private final ScriptRepository scriptRepository;
    private final int streamCapacity;
    private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
    private final String[] illegalNamespace = new String[] { "swagger-ui" };

    @Autowired
    public ScriptServiceImpl(ScriptRepository scriptRepository,
            @org.springframework.beans.factory.annotation.Value("${scripts.outputStream.capacity}") int streamCapacity) {
        this.scriptRepository = scriptRepository;
        this.streamCapacity = streamCapacity;
    }

    /**
     * A method to get a script page with specified parameters
     * 
     * @return Page with specified parameters
     */
    public List<Script> getScriptList(ScriptStatus scriptStatus, String nameContains, boolean orderByName,
            boolean reverseOrder) {
        Comparator<Script> comparator;
        if (orderByName)
            comparator = Comparator.comparing(Script::getName);
        else
            comparator = Comparator.comparing(Script::getCreateTime);
        if (reverseOrder)
            comparator = comparator.reversed();
        List<Script> scriptList = scriptRepository.getScriptList(scriptStatus, nameContains).stream().sorted(comparator)
                .collect(Collectors.toList());
        logger.debug(
                "Script service return filtered and sorted script list. Parameters [scriptStatus={}, nameContains={}, orderByName={}, reverseOrder={}]",
                scriptStatus, nameContains, orderByName, reverseOrder);
        return scriptList;
    }

    /**
     * A method for checking a script for validity and adding it to the list of scripts
     * 
     * @param scriptName
     *            script name (identifier)
     * @param scriptCode
     *            JS script
     * 
     * @return Script with information about the script
     * 
     * @throws WrongScriptException
     *             if script has syntax error
     */
    @Override
    public Script addScript(String scriptName, String scriptCode) {
        checkName(scriptName);
        Script script = Script.create(scriptName, scriptCode, streamCapacity);
        scriptRepository.putScript(scriptName, script);
        logger.debug("[{}] - New script was added to the service", scriptName);
        return script;
    }

    /**
     * Method for running the script in asynchronous mode
     * 
     * @param script
     *            Script of the script to run
     */
    @Override
    public void startScriptAsynchronously(Script script) {
        logger.debug("[{}] - Starting script in asynchronously mode", script.getName());
        script.run();
    }

    /**
     * Method for running the script in synchronous mode
     * 
     * @param script
     *            Script of the script to run
     */
    @Override
    public void startScriptSynchronously(Script script) {
        logger.debug("[{}] - Starting script in synchronously mode", script.getName());
        script.run();
    }

    /**
     * A method to get information about the script you are looking for
     * 
     * @param scriptName
     *            script name (identifier)
     * 
     * @return ScriptInfoForSingle with information for a single display
     */
    @Override
    public Script getScript(String scriptName) {
        Script script = scriptRepository.getScript(scriptName);
        logger.debug("[{}] - Returning the script", scriptName);
        return script;
    }

    /**
     * Method for stopping a running script
     * 
     * @param scriptName
     *            script name (identifier)
     */
    @Override
    public void stopScript(String scriptName) {
        scriptRepository.getScript(scriptName).stopScriptExecution();
        logger.debug("[{}] - Script execution canceled", scriptName);
    }

    /**
     * Method for removing a script from the list
     * 
     * @param scriptName
     *            script name (identifier)
     * 
     * @throws WrongScriptException
     *             if script is running
     */
    @Override
    public void deleteScript(String scriptName) {
        final Script script = scriptRepository.getScript(scriptName);
        synchronized (script) {
            if (script.getStatus() == ScriptStatus.RUNNING)
                throw new WrongScriptStatusException("To delete a running script, you must first stop it",
                        script.getStatus());
            script.closeContext();
            scriptRepository.deleteScript(scriptName);
        }
        logger.debug("[{}] - Script deleted from the service", script.getName());
    }

    /**
     * A method for checking a script name for forbidden words and characters
     * 
     * @param scriptName
     *            script name (identifier)
     * 
     * @throws WrongNameException
     *             if script name contains forbidden words and symbols
     */
    private void checkName(String scriptName) {
        if (!correctlyScriptName.matcher(scriptName).matches())
            throw new WrongNameException("The name uses illegal characters or exceeds the allowed length");
        for (String name : illegalNamespace) {
            if (name.equals(scriptName))
                throw new WrongNameException("This name is reserved and is forbidden for use");
        }
        logger.trace("[{}] - Name verification completed successfully", scriptName);
    }

}
