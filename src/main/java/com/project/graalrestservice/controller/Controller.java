package com.project.graalrestservice.controller;

import com.project.graalrestservice.repositories.ScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @Autowired
    ScriptExecutor scriptExecutor;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String appStatus() {
        return "The application is running";
    }

}
