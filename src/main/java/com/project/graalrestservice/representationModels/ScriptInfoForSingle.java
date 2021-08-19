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

    private String name;
    private ScriptStatus status;
    private OffsetDateTime createTime;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String logsLink;

    public ScriptInfoForSingle() {
    }

    private void setLinks() {
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

    public void setName(String name) {
        this.name = name;
        setLinks();
    }
    public void setStatus(ScriptStatus status) {
        this.status = status;
    }
    public void setCreateTime(OffsetDateTime createTime) {
        this.createTime = createTime;
    }
    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }
    public void setLogsLink(String logsLink) {
        this.logsLink = logsLink;
    }
}
