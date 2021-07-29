package com.project.graalrestservice.controller;

import com.project.graalrestservice.repositories.ScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    ScriptExecutor scriptExecutor;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String appStatus() {
        return "The application is running";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String executeScript(@RequestBody String script) {
        return scriptExecutor.execute(script);
    }

}
