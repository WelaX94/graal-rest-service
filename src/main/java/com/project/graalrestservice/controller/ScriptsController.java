package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.exceptions.PageDoesNotExistException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongArgumentException;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.representationModels.mappers.ListScriptMapper;
import com.project.graalrestservice.representationModels.mappers.SingleScriptMapper;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/** Controller class responsible for "/scripts" */
@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    private static final Logger logger = LoggerFactory.getLogger(ScriptsController.class);
    private static final String scriptRequestProcessed = "[{}] - Request successfully processed";
    private static final String parameters = "Parameters: [page={}, pageSize={}, status={}, nameContains={}, orderByName={}, reverseOrder={}]";
    private static final String mdcNameIdentifier = "scriptName";
    private final ScriptService scriptService;

    @Autowired
    public ScriptsController(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * A method to get a list of scripts
     * @param pageSize maximum number of scripts per page
     * @param page number of page
     * @return page with filtered and sorted scripts
     * */
    @GetMapping
    public ResponseEntity<Page<List<ScriptInfoForList>>> getScriptListPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nameContains,
            @RequestParam(defaultValue = "false") boolean orderByName,
            @RequestParam(defaultValue = "false") boolean reverseOrder) {
        logger.info(
                "Script list request received. " + parameters, page, pageSize, status, nameContains, orderByName, reverseOrder);
        MDC.put(mdcNameIdentifier, "GetScriptsMethod");
        List<Script> scriptList = scriptService.getScriptList(ScriptStatus.getStatus(status), nameContains, orderByName, reverseOrder);
        Page<List<ScriptInfoForList>> scriptPage = convertListToPage(scriptList, page, pageSize, status, nameContains, orderByName, reverseOrder);
        logger.info("Script list request successfully processed. " + parameters, page, pageSize, status, nameContains, orderByName, reverseOrder);
        return new ResponseEntity<>(scriptPage, HttpStatus.OK);
    }

    /**
     * Method for adding a new script to the run queue
     * @param scriptCode JS script
     * @param scriptName script name (identifier)
     * @return JSON information about script
     * @see ScriptsController#runScriptWithLogsStreaming(String, String)
     * */
    @PutMapping(value = "/{scriptName}")
    public ResponseEntity<ScriptInfoForSingle> runScript(
            @RequestBody String scriptCode,
            @PathVariable String scriptName) {
        logger.info("[{}] - A new script is requested to run", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        Script script = scriptService.addScript(scriptName, scriptCode);
        scriptService.startScriptAsynchronously(script);
        ScriptInfoForSingle scriptInfoForSingle = SingleScriptMapper.forSingle.map(script);
        scriptInfoForSingle.setLinks();
        logger.info(scriptRequestProcessed, scriptName);
        return new ResponseEntity<>(scriptInfoForSingle, HttpStatus.CREATED);
    }

    /**
     * A method for obtaining information about the script
     * @param scriptName script name (identifier)
     * @return JSON information about script
     */
    @GetMapping(value = "/{scriptName}")
    public ResponseEntity<ScriptInfoForSingle> getSingleScriptInfo(@PathVariable String scriptName) {
        logger.info("[{}] - Single script info request received", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        ScriptInfoForSingle scriptInfoForSingle = SingleScriptMapper.forSingle.map(scriptService.getScript(scriptName));
        scriptInfoForSingle.setLinks();
        logger.info(scriptRequestProcessed, scriptName);
        return new ResponseEntity<>(scriptInfoForSingle, HttpStatus.OK);
    }

    /**
     * A method for retrieving the script logs
     * @param scriptName script name (identifier)
     * @return script logs
     */
    @GetMapping(value = "/{scriptName}/logs")
    public ResponseEntity<String> getScriptLogs(
            @PathVariable String scriptName,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false) Integer to) {
        logger.info("[{}] - Script logs request received (from={}, to={})", scriptName, from, to);
        MDC.put(mdcNameIdentifier, scriptName);
        String logs = scriptService.getScript(scriptName).getOutputLogs();
        if (to == null) to = logs.length();
        try {
            if (from.equals(to)) throw new StringIndexOutOfBoundsException();
            logs = logs.substring(from, to);
            logger.trace("[{}] - Try block passed without errors", scriptName);
        } catch (StringIndexOutOfBoundsException e) {
            logger.debug("[{}] - Caught StringIndexOutOfBoundsException. Indexes entered incorrectly", scriptName);
            throw new WrongArgumentException("The 'from-to' range is entered incorrectly");
        }
        logger.info(scriptRequestProcessed, scriptName);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping(value = "/{scriptName}/script")
    public ResponseEntity<String> getScriptCode(@PathVariable String scriptName) {
        logger.info("[{}] - Script code request received", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        String scriptCode = scriptService.getScript(scriptName).getScriptCode();
        logger.info(scriptRequestProcessed, scriptName);
        return new ResponseEntity<>(scriptCode, HttpStatus.OK);
    }

    /**
     * Another option for adding a new script to the run queue in the blocking variant ({@link ScriptsController#runScript(String, String) first option})
     * @param scriptCode JS script
     * @param scriptName script name (identifier)
     * @return the log broadcast in real time
     * @see ScriptsController#runScript(String, String)
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping(value = "/{scriptName}/logs")
    public StreamingResponseBody runScriptWithLogsStreaming(
            @RequestBody String scriptCode,
            @PathVariable String scriptName) {
        logger.info("[{}] - Script run with logs streaming request received", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        Script script = scriptService.addScript(scriptName, scriptCode);
        logger.info(scriptRequestProcessed, scriptName);
        return (OutputStream outputStream) -> {
            logger.info("[{}] - Streaming logs started", scriptName);
            script.addStreamForRecording(outputStream);
            try {
                outputStream.flush();
                scriptService.startScriptAsynchronously(script);
                while (script.getStatus() == ScriptStatus.IN_QUEUE || script.getStatus() == ScriptStatus.RUNNING) {
                    Thread.sleep(100);
                }
            } catch (ClientAbortException | InterruptedException e) {
                logger.info("[{}] - Client terminated the connection", scriptName);
            } finally {
                script.deleteStreamForRecording(outputStream);
            }
            logger.info("[{}] - Streaming logs finished", scriptName);
        };
    }

    /**
     * Method for stopping a running script
     * @param scriptName script name (identifier)
     */
    @PostMapping(value = "/{scriptName}")
    public void stopScript(@PathVariable String scriptName) {
        logger.info("[{}] - Stop script request received", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        scriptService.stopScript(scriptName);
        logger.info(scriptRequestProcessed, scriptName);
    }

    /**
     * Method for deleting script from script list
     * @param scriptName script name (identifier)
     */
    @DeleteMapping(value = "/{scriptName}")
    public void deleteScript(@PathVariable String scriptName) {
        logger.info("[{}] - Delete script request received", scriptName);
        MDC.put(mdcNameIdentifier, scriptName);
        scriptService.deleteScript(scriptName);
        logger.info(scriptRequestProcessed, scriptName);
    }

    private Page<List<ScriptInfoForList>> convertListToPage(List<Script> scriptList, int pageNumber, int pageSize, String status, String nameContains, boolean orderByName, boolean reverseOrder) {
        logger.trace("Starts converting List<Script> to Page<List<ScriptInfoForList>>. " + parameters, pageNumber, pageSize, status, nameContains, orderByName, reverseOrder);

        if (pageNumber < 1) throw new WrongArgumentException("The page number cannot be less than 1");
        if (pageSize < 1) throw new WrongArgumentException("The page size cannot be less than 1");

        int listSize = scriptList.size();
        int end = pageNumber * pageSize;
        int start = end - pageSize;
        if (start >= listSize) throw new PageDoesNotExistException(pageNumber);
        if (listSize < end) end = listSize;

        List<ScriptInfoForList> pageList = new ArrayList<>();
        for( ; start < end; start++) {
            ScriptInfoForList sifl = ListScriptMapper.forList.map(scriptList.get(start));
            sifl.setLinks();
            pageList.add(sifl);
        }

        int numPages = (listSize % pageSize == 0) ? (listSize / pageSize) : (listSize / pageSize + 1);
        Page<List<ScriptInfoForList>> scriptsPage = new Page<>(pageList, pageNumber, numPages, listSize);
        scriptsPage.setLinks(pageSize, status, nameContains, orderByName, reverseOrder);

        logger.trace("Converting List<Script> to Page<List<ScriptInfoForList>> completed successfully. " + parameters, pageNumber, pageSize, status, nameContains, orderByName, reverseOrder);
        return scriptsPage;
    }

}
