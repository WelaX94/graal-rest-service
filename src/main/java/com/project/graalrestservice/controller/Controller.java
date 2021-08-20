package com.project.graalrestservice.controller;

import com.project.graalrestservice.representationModels.RootInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/** Controller class responsible for "/" */
@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    /**
     * Method responsible for "/"
     * @return JSON with application status, self link and link to script list
     * */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RootInfo appStatus() {
        logger.info("Root request successfully processed");
        return new RootInfo("The application is running");
    }

}
