package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ScriptInfoForSingle extends RepresentationModel<ScriptInfoForSingle> {

    private final String name;
    private final ScriptStatus status;
    private final LocalDateTime createTime;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String logs;

    public ScriptInfoForSingle(ScriptInfo script) {
        this.name = script.getName();
        this.status = script.getScriptStatus();
        this.createTime = script.getCreateTime();
        this.startTime = script.getStartTime();
        this.endTime = script.getEndTime();
        this.logs = script.getLink() + "/logs";
        add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(name)).withSelfRel());
        add(linkTo(ScriptsController.class).withRel("scriptList"));
    }

    public String getName() {
        return name;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public String getLogs() {
        return logs;
    }

}
