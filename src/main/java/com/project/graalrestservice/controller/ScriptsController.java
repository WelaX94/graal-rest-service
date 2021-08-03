package com.project.graalrestservice.controller;

import com.project.graalrestservice.repositories.ScriptHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    @Autowired
    ScriptHandler scriptHandler;

    @RequestMapping(method = RequestMethod.GET)
    public String getScriptList() {
        return scriptHandler.getAllScripts();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public String runScript(@RequestBody String script, @PathVariable String scriptName) {
        return scriptHandler.addScript(scriptName, script);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public String getScriptInfo(@PathVariable String scriptName) {
        return scriptHandler.getScriptInfo(scriptName);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public String stopScript(@PathVariable String scriptName) {
        return scriptHandler.stopScript(scriptName);
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public String deleteScript(@PathVariable String scriptName) {
        return scriptHandler.deleteScript(scriptName);
    }

}
