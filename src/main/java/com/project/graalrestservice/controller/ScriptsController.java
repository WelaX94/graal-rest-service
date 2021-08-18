package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import com.project.graalrestservice.domain.scriptHandler.utils.CircularOutputStream;
import com.project.graalrestservice.domain.scriptHandler.utils.QueueOutputStream;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.List;

/** Controller class responsible for "/scripts" */
@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    private static final Logger logger = LogManager.getLogger(ScriptsController.class);
    private final ScriptService scriptService;
    private final int streamCapacity;

    @Autowired
    public ScriptsController(ScriptService scriptService, @Value("${scripts.outputStream.capacity}") int streamCapacity) {
        this.scriptService = scriptService;
        this.streamCapacity = streamCapacity;
    }

    /**
     * A method to get a list of scripts
     * @param filters list of filters to filter and sort the list of scripts
     * @param pageSize maximum number of scripts per page
     * @param page number of page
     * @return page with filtered and sorted scripts
     * */
    @RequestMapping(method = RequestMethod.GET)
    public Page<List<ScriptInfoForList>> getScriptListPage(
            @RequestParam(defaultValue = "basic") String filters,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int page) {
        logger.info(String.format
                ("Script list request received: filters=%s, pageSize=%d, page=%d", filters, pageSize, page));
        Page<List<ScriptInfoForList>> scriptListPage = scriptService.getScriptListPage(filters, pageSize, page);
        logger.info("Request successfully processed");
        return scriptListPage;
    }

    /**
     * Method for adding a new script to the run queue
     * @param script JS script
     * @param scriptName script name (identifier)
     * @param sync run script type - asynchronous (true) or synchronous (false)
     * @param request Http Servlet Request
     * @return JSON information about script
     * @see ScriptsController#runScriptWithLogsStreaming(String, String, HttpServletRequest)
     * */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public ResponseEntity<ScriptInfoForSingle> runScript(
            @RequestBody String script,
            @PathVariable String scriptName,
            @RequestParam(defaultValue = "true") boolean sync,
            HttpServletRequest request) {
        logger.info("A new script is requested to run");
        Script scriptInfo =
                scriptService.addScript(scriptName, script, request.getRequestURL().append("/logs").toString());
        if (sync) scriptService.startScriptAsynchronously(scriptInfo);
        else scriptService.startScriptSynchronously(scriptInfo);
        logger.info("Request successfully processed");
        return (sync) ?
                (new ResponseEntity<>(new ScriptInfoForSingle(scriptInfo), HttpStatus.ACCEPTED)) :
                (new ResponseEntity<>(new ScriptInfoForSingle(scriptInfo), HttpStatus.CREATED));
    }

    /**
     * A method for obtaining information about the script
     * @param scriptName script name (identifier)
     * @return JSON information about script
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public ScriptInfoForSingle getSingleScriptInfo(@PathVariable String scriptName) {
        logger.info("Single script info request received");
        ScriptInfoForSingle scriptInfoForSingle = scriptService.getScriptInfo(scriptName);
        logger.info("Request successfully processed");
        return scriptInfoForSingle;
    }

    /**
     * A method for retrieving the script logs
     * @param scriptName script name (identifier)
     * @return script logs
     */
    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.GET)
    public String getScriptLogs(@PathVariable String scriptName) {
        logger.info("Script logs request received");
        String logs = scriptService.getScriptLogs(scriptName);
        logger.info("Request successfully processed");
        return logs;
    }

    /**
     * Another option for adding a new script to the run queue in the blocking variant ({@link ScriptsController#runScript(String, String, boolean, HttpServletRequest) first option})
     *
     * @param scriptCode JS script
     * @param scriptName script name (identifier)
     * @param request HttpServletRequest
     * @return the log broadcast in real time
     * @see ScriptsController#runScript(String, String, boolean, HttpServletRequest)
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.PUT)
    public StreamingResponseBody runScriptWithLogsStreaming(
            @RequestBody String scriptCode,
            @PathVariable String scriptName,
            HttpServletRequest request) {
        logger.info("Script run with logs streaming request received");
        Script script = scriptService.addScript(scriptName, scriptCode, request.getRequestURL().toString());
        logger.info("Request successfully processed");
        return (OutputStream outputStream) -> {
            QueueOutputStream queueOutputStream = new QueueOutputStream(streamCapacity);
            script.addStreamForRecording(queueOutputStream);
            scriptService.startScriptAsynchronously(script);
            outputStream.flush();
            try {
                while (script.getScriptStatus() == ScriptStatus.IN_QUEUE || script.getScriptStatus() == ScriptStatus.RUNNING || queueOutputStream.hasNextByte()) {
                    outputStream.write(queueOutputStream.readBytes());
                    outputStream.flush();
                }
            } catch (ClientAbortException e) {
                System.out.println("CLIENT ABORT EXCEPTION");
            } finally {
                queueOutputStream.clearStream();
                script.deleteStreamForRecording(queueOutputStream);
            }
        };
    }

    /**
     * Method for stopping a running script
     * @param scriptName script name (identifier)
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public void stopScript(@PathVariable String scriptName) {
        logger.info("Stop script request received");
        scriptService.stopScript(scriptName);
        logger.info("Request successfully processed");
    }

    /**
     * Method for deleting script from script list
     * @param scriptName script name (identifier)
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public void deleteScript(@PathVariable String scriptName) {
        logger.info("Delete script request received");
        scriptService.deleteScript(scriptName);
        logger.info("Request successfully processed");
    }

}
