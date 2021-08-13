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
            ScriptInfo scriptInfo = scriptService.addScript(scriptName, script, request.getRequestURL().append("/logs").toString());
            if (api.equals("f")) scriptService.startScriptAsynchronously(scriptInfo);
            else scriptService.startScriptSynchronously(scriptInfo);
            LOGGER.info(String.format("Request[%d] successfully processed", id));
            return new ScriptInfoForSingle(scriptInfo);
        }
        else throw new WrongArgumentException("Unknown API option: " + api);
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
        String logs = scriptService.getScriptLogs(scriptName);
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return logs;
    }

    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.PUT)
    public StreamingResponseBody runScriptWithLogsStreaming(
            @RequestBody String script,
            @PathVariable String scriptName,
            HttpServletRequest request) {
        int id = getId();
        LOGGER.info(String.format("Script run with logs streaming request[%d] received", id));
        ScriptInfo scriptInfo = scriptService.addScript(scriptName, script, request.getRequestURL().toString());
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptInfo;
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





    @RequestMapping(value = "/{scriptName}/lo", method = RequestMethod.GET)
    public StreamingResponseBody runScriptWithLogsStreamingAAAA(
            @PathVariable String scriptName,
            HttpServletRequest request) {
        int id = getId();
        LOGGER.info(String.format("Script run with logs streaming request[%d] received", id));
        ScriptInfo scriptInfo = scriptService.addScript(scriptName, "function wait(ms){\n" +
                "   var start = new Date().getTime();\n" +
                "   var end = start;\n" +
                "   while(end < start + ms) {\n" +
                "     end = new Date().getTime();\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "for(let a = 0; a < 10; a++) {\n" +
                "    console.log(a);\n" +
                "    wait(100);\n" +
                "}", request.getRequestURL().toString());
        LOGGER.info(String.format("Request[%d] successfully processed", id));
        return scriptInfo;
    }







}
