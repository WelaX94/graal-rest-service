package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;

/** Controller class responsible for "/scripts" */
@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    /** SelfJ logger responsible for this class*/
    private final static Logger LOGGER = LogManager.getLogger(ScriptsController.class);
    private int requestId = 0;

    /** ScriptService bean*/
    @Autowired
    private ScriptService scriptService;

    /**
     * A method to get a list of scripts
     * @param filters list of filters to filter and sort the list of scripts
     * @param pageSize maximum number of scripts per page
     * @param page number of page
     * @return page with filtered and sorted scripts
     * */
    @RequestMapping(method = RequestMethod.GET)
    public ScriptListPage getScriptListPage(
            @RequestParam(required=false) String filters,
            @RequestParam(required=false) Integer pageSize,
            @RequestParam(required=false) Integer page) {
        int id = getId();
        LOGGER.info(String.format
                ("Script list request[%d] received: filters=%s, pageSize=%d, page=%d", id, filters, pageSize, page));
        ScriptListPage scriptListPage = scriptService.getScriptListPage(filters, pageSize, page);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptListPage;
    }

    /**
     * Method for adding a new script to the run queue
     * @param script JS script
     * @param scriptName script name (identifier)
     * @param api selecting a blocking or non-blocking api
     * @param request Http Servlet Request
     * @return JSON information about script
     * @see ScriptsController#runScriptWithLogsStreaming(String, String, HttpServletRequest)
     * */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public ScriptInfoForSingle runScript(
            @RequestBody String script,
            @PathVariable String scriptName,
            @RequestParam(required=false) String api,
            HttpServletRequest request) {
        int id = getId();
        LOGGER.info(String.format("A new script is requested[%d] to run", id));
        if (api == null) api = "f";
        api = api.toLowerCase();
        if (api.equals("b") || api.equals("f") ) {
            ScriptInfo scriptInfo = scriptService.addScript(scriptName, script, request.getRequestURL().append("/logs").toString(), false);
            if (api.equals("f")) scriptService.startScriptAsynchronously(scriptInfo);
            else scriptService.startScriptSynchronously(scriptInfo);
            LOGGER.info(String.format("Request[%d] successfully processed", id));
            return new ScriptInfoForSingle(scriptInfo);
        }
        else throw new WrongArgumentException("Unknown API option: " + api);
    }

    /**
     * A method for obtaining information about the script
     * @param scriptName script name (identifier)
     * @return JSON information about script
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public ScriptInfoForSingle getSingleScriptInfo(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Single script info request[%d] received", id));
        ScriptInfoForSingle scriptInfoForSingle = scriptService.getScriptInfo(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptInfoForSingle;
    }

    /**
     * A method for retrieving the script logs
     * @param scriptName script name (identifier)
     * @return script logs
     */
    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.GET)
    public String getScriptLogs(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Script logs request[%d] received", id));
        String logs = scriptService.getScriptLogs(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return logs;
    }

    /**
     * Another option for adding a new script to the run queue in the blocking variant ({@link ScriptsController#runScript(String, String, String, HttpServletRequest) first option})
     *
     * @param script JS script
     * @param scriptName script name (identifier)
     * @param request HttpServletRequest
     * @return the log broadcast in real time
     * @see ScriptsController#runScript(String, String, String, HttpServletRequest)
     */
    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.PUT)
    public StreamingResponseBody runScriptWithLogsStreaming(
            @RequestBody String script,
            @PathVariable String scriptName,
            HttpServletRequest request) {
        int id = getId();
        LOGGER.info(String.format("Script run with logs streaming request[%d] received", id));
        ScriptInfo scriptInfo = scriptService.addScript(scriptName, script, request.getRequestURL().toString(), true);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptInfo;
    }

    /**
     * Method for stopping a running script
     * @param scriptName script name (identifier)
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public void stopScript(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Stop script request[%d] received", id));
        scriptService.stopScript(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
    }

    /**
     * Method for deleting script from script list
     * @param scriptName script name (identifier)
     */
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public void deleteScript(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Delete script request[%d] received", id));
        scriptService.deleteScript(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
    }

    /**
     * Method for getting request id. Used for logging
     * @return unique id
     */
    private synchronized int getId() {
        return requestId++;
    }

}
