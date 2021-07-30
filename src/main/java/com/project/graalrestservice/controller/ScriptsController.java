package com.project.graalrestservice.controller;

import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
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
    ScriptHandler scriptHandler;

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
        ScriptInfo scriptInfo = scriptHandler.addScript(scriptName, script);
        return "The script is received and added to the execution queue.\nDetailed information: " + scriptInfo.getLink();
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
