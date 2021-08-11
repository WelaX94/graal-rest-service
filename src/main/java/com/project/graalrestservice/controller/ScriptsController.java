package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    private final static Logger LOGGER = LogManager.getLogger(ScriptsController.class);
    private int requestId = 0;

    @Autowired
    private ScriptService scriptService;

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

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public String runScript(@RequestBody String script, @PathVariable String scriptName, HttpServletRequest request) {
        int id = getId();
        LOGGER.info(String.format("A new script is requested[%d] to run", id));
        ScriptInfo scriptInfo = scriptService.addScript(scriptName, script, request.getRequestURL().toString());
        String output = scriptService.startScript(scriptInfo);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return output;
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public ScriptInfoForSingle getSingleScriptInfo(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Single script info request[%d] received", id));
        ScriptInfoForSingle scriptInfoForSingle = scriptService.getScriptInfo(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptInfoForSingle;
    }

    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.GET)
    public String getScriptLogs(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Script logs request[%d] received", id));
        String logs = scriptService.getScriptInfo(scriptName).returnFullLogs();
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return logs;
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public void stopScript(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Stop script request[%d] received", id));
        scriptService.stopScript(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public void deleteScript(@PathVariable String scriptName) {
        int id = getId();
        LOGGER.info(String.format("Delete script request[%d] received", id));
        scriptService.deleteScript(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
    }

    private synchronized int getId() {
        return requestId++;
    }

}
