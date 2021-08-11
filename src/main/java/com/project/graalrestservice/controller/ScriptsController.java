package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    @Autowired
    ScriptService scriptService;

    @RequestMapping(method = RequestMethod.GET)
    public ScriptListPage getScriptListPage(
            @RequestParam(required=false) String filters,
            @RequestParam(required=false) Integer pageSize,
            @RequestParam(required=false) Integer page) {
        return scriptService.getScriptListPage(filters, pageSize, page);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public String runScript(@RequestBody String script, @PathVariable String scriptName, HttpServletRequest request) {
        return scriptService.addScript(scriptName, script, request.getRequestURL().toString());
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public ScriptInfoForSingle getSingleScriptInfo(@PathVariable String scriptName) {
        return scriptService.getScriptInfo(scriptName);
    }

    @RequestMapping(value = "/{scriptName}/logs", method = RequestMethod.GET)
    public String getScriptLogs(@PathVariable String scriptName) {
        return scriptService.getScriptInfo(scriptName).returnFullLogs();
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public void stopScript(@PathVariable String scriptName) {
        scriptService.stopScript(scriptName);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public void deleteScript(@PathVariable String scriptName) {
        scriptService.deleteScript(scriptName);
    }

}
