package com.project.graalrestservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String appStatus() {
        return "The application is running";
    }

}
