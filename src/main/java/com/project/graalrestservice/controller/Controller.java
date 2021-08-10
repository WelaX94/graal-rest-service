package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.models.representation.Info;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Info appStatus() {
        return new Info("The application is running");
    }

}
