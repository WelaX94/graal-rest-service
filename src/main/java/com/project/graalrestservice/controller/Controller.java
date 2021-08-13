package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.models.representation.Info;
import org.springframework.web.bind.annotation.*;

/** Controller class responsible for "/" */
@RestController
public class Controller {

    /**
     * Method responsible for "/"
     * @return JSON with application status, self link and link to script list
     * */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Info appStatus() {
        return new Info("The application is running");
    }

}
