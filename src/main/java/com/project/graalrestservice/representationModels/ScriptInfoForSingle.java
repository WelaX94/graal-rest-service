package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying information about one script
 */
public class ScriptInfoForSingle extends RepresentationModel<ScriptInfoForSingle> {

    private final String name;
    private final ScriptStatus status;
    private final LocalDateTime createTime;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String logsLink;

    /**
     * Basic constructor
     * @param script the main scriptInfo containing all the information about the script
     */
    public ScriptInfoForSingle(ScriptInfo script) {
        this.name = script.getName();
        this.status = script.getScriptStatus();
        this.createTime = script.getCreateTime();
        this.startTime = script.getStartTime();
        this.endTime = script.getEndTime();
        this.logsLink = script.getLogsLink();
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
    public String getLogsLink() {
        return logsLink;
    }

}
