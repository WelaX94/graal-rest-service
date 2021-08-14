package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * A class for processing commands over scripts
 */
@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ExecutorService executorService;

    @org.springframework.beans.factory.annotation.Value("${scriptOutputStream.capacity}")
    private int streamCapacity;

    private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
    private final String[] illegalNamespace = new String[]{"swagger-ui"};

    /**
     * A method to get a script page with specified parameters
     * @param filters filter list
     * @param pageSize page size
     * @param page page
     * @return ScriptListPage with specified parameters
     */
    @Override
    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page) {
        return scriptRepository.getScriptListPage(filters, pageSize, page);
    }

    /**
     * A method for checking a script for validity and adding it to the list of scripts
     * @param scriptName script name (identifier)
     * @param script JS script
     * @param logsLink link to script output logs page
     * @param readable parameter indicates whether the logs will be read in real time or not
     * @return ScriptInfo with information about the script
     * @throws WrongScriptException if script has syntax error
     */
    @Override
    public ScriptInfo addScript(String scriptName, String script, String logsLink, boolean readable) {
        checkName(scriptName);
        CircularOutputStream outputStream = new CircularOutputStream(streamCapacity, readable);
        Context context = Context.newBuilder().out(outputStream).err(outputStream).allowCreateThread(true).build();
        try {
            Value value = context.parse("js", script);
            ScriptInfo scriptInfo = new ScriptInfo(scriptName, script, logsLink, outputStream, value, context, executorService);
            scriptRepository.put(scriptName, scriptInfo);
            return scriptInfo;
        } catch (PolyglotException e) {
            context.close();
            throw new WrongScriptException(e.getMessage());
        }
    }

    /**
     * Method for running the script in asynchronous mode
     * @param scriptInfo ScriptInfo of the script to run
     */
    @Override
    public void startScriptAsynchronously(ScriptInfo scriptInfo) {
        executorService.execute(scriptInfo);
    }

    /**
     * Method for running the script in synchronous mode
     * @param scriptInfo ScriptInfo of the script to run
     */
    @Override
    public void startScriptSynchronously(ScriptInfo scriptInfo) {
        scriptInfo.run();
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
        final ScriptInfo scriptInfo = scriptRepository.get(scriptName);
        synchronized (scriptInfo) {
            if (scriptInfo.getScriptStatus() == ScriptStatus.RUNNING) throw new WrongScriptStatusException
                    ("To delete a running script, you must first stop it", scriptInfo.getScriptStatus());
            scriptInfo.closeContext();
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

    public ScriptServiceImpl() {
    }

    public ScriptServiceImpl(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

}
