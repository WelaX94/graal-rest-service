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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptRepository scriptRepository;

    private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");
    private final String[] illegalNamespace = new String[]{"filter"};

    @Override
    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page) {
        return scriptRepository.getScriptListPage(filters, pageSize, page);
    }

    @Override
    public ScriptInfo addScript(String scriptName, String script, String link) {
        checkName(scriptName);
        OutputStream outputStream = new CircularOutputStream(65536);
        Context context = Context.newBuilder().out(outputStream).err(outputStream).allowCreateThread(true).build();
        try {
            Value value = context.parse("js", script);
            ScriptInfo scriptInfo = new ScriptInfo(scriptName, script, link, outputStream, value, context);
            scriptRepository.put(scriptName, scriptInfo);
            return scriptInfo;
        } catch (PolyglotException e) {
            context.close();
            throw new WrongScriptException(e.getMessage());
        }
    }

    @Override
    @Async
    public String startScriptAsynchronously(ScriptInfo scriptInfo) {
        scriptInfo.runScript();
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    public String startScriptSynchronously(ScriptInfo scriptInfo) {
        scriptInfo.runScript();
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
    }

    @Override
    public ScriptInfoForSingle getScriptInfo(String scriptName) {
        return new ScriptInfoForSingle(scriptRepository.get(scriptName));
    }

    @Override
    public void stopScript(String scriptName) {
        scriptRepository.get(scriptName).stopScriptExecution();
    }

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
