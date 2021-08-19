package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import org.springframework.hateoas.RepresentationModel;

import java.time.OffsetDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying information about one script
 */
public class ScriptInfoForSingle extends RepresentationModel<ScriptInfoForSingle> {

    private final String name;
    private final ScriptStatus status;
    private final OffsetDateTime createTime;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;
    private final String logsLink;

    /**
     * Basic constructor
     * @param script the main scriptInfo containing all the information about the script
     */
    public ScriptInfoForSingle(Script script) {
        this.name = script.getName();
        this.status = script.getScriptStatus();
        this.createTime = script.getCreateTime();
        this.startTime = script.getStartTime();
        this.endTime = script.getEndTime();
        this.logsLink = null;
        add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(name)).withSelfRel());
        add(linkTo(ScriptsController.class).withRel("scriptList"));
    }

    public String getName() {
        return name;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public String getCreateTime() {
        return createTime.toString();
    }
    public String getStartTime() {
        return (startTime == null) ? null : startTime.toString();
    }
    public String getEndTime() {
        return (endTime == null) ? null : endTime.toString();
    }
    public String getLogsLink() {
        return logsLink;
    }

}
