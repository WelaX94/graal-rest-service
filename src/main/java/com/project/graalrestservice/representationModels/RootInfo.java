package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.Controller;
import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * A class for outputting single messages in JSON format
 */
public class RootInfo extends RepresentationModel<RootInfo> {

    private String info;

    public RootInfo(String info) {
        this.info = info;
        add(linkTo(Controller.class).withSelfRel());
        add(linkTo(ScriptsController.class).withRel("scriptList"));
    }

    public String getInfo() {
        return info;
    }

}
