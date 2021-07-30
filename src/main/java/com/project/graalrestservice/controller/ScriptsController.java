package com.project.graalrestservice.controller;

import com.project.graalrestservice.repositories.ScriptExecutor;
import com.project.graalrestservice.repositories.ScriptList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scripts")
public class ScriptsController {

    @Autowired
    ScriptList scriptList;

    @Autowired
    ScriptExecutor scriptExecutor;

    @RequestMapping(method = RequestMethod.GET)
    public String getScriptList() {
        return scriptList.toString();
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String getInfo() {
        return "INFO PAGE";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/{scriptName}", method = RequestMethod.PUT)
    public String runScript(@RequestBody String script, @PathVariable String scriptName) {
        return "RUN SCRIPT PAGE";
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.GET)
    public String getScriptInfo(@PathVariable String scriptName) {
        return "SCRIPT INFO PAGE";
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.POST)
    public String stopScript(@PathVariable String scriptName) {
        return "STOP SCRIPT PAGE";
    }

    @RequestMapping(value = "/{scriptName}", method = RequestMethod.DELETE)
    public String deleteScript(@PathVariable String scriptName) {
        return "DELETE SCRIPT PAGE";
    }

}
