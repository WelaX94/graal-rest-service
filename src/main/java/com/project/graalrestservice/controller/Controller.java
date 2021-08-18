package com.project.graalrestservice.controller;

import com.project.graalrestservice.representationModels.RootInfo;
import org.springframework.web.bind.annotation.*;

/** Controller class responsible for "/" */
@RestController
public class Controller {

    /**
     * Method responsible for "/"
     * @return JSON with application status, self link and link to script list
     * */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RootInfo appStatus() {
        return new RootInfo("The application is running");
    }

}
