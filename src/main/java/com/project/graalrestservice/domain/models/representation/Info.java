package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.controller.Controller;
import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class Info extends RepresentationModel<Info> {

    private String info;

    public Info(String info) {
        this.info = info;
        add(linkTo(Controller.class).withSelfRel());
        add(linkTo(ScriptsController.class).withRel("scriptList"));
    }

    public String getInfo() {
        return info;
    }
}
