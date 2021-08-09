package com.project.graalrestservice.controller;

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
    public String getScriptList() {
        return scriptService.getAllScripts();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public String runScript(@RequestBody String script, @PathVariable String scriptName, HttpServletRequest request) {
        return scriptService.addScript(scriptName, script, request.getRequestURL().toString());
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public String getScriptInfo(@PathVariable String scriptName) {
        return scriptService.getScriptInfo(scriptName);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public String stopScript(@PathVariable String scriptName) {
        return scriptService.stopScript(scriptName);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public String deleteScript(@PathVariable String scriptName) {
        return scriptService.deleteScript(scriptName);
    }

}
