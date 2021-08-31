package com.project.graalrestservice.web.controller;

import com.project.graalrestservice.web.dto.RootInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/** RootController class responsible for "/" */
@RestController
public class RootController {

  private static final Logger logger = LoggerFactory.getLogger(RootController.class);

  /**
   * Method responsible for "/"
   * 
   * @return JSON with application status, self link and link to script list
   */
  @GetMapping(value = "/")
  public RootInfo appStatus() {
    logger.debug("Root request successfully processed");
    return new RootInfo("The application is running");
  }

}
