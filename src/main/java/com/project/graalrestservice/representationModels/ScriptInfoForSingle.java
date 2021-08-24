package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import org.springframework.hateoas.RepresentationModel;

import java.time.OffsetDateTime;
import java.util.Objects;

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
  private int logsSize;

  public void setLinks() {
    add(linkTo(methodOn(ScriptsController.class).getScriptLogs(name, null, null)).withRel("logs")
        .expand());
    add(linkTo(methodOn(ScriptsController.class).getScriptCode(name)).withRel("script"));
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

  public int getLogsSize() {
    return logsSize;
  }

  public void setName(String name) {
    this.name = name;
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

  public void setLogsSize(int logsSize) {
    this.logsSize = logsSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    ScriptInfoForSingle that = (ScriptInfoForSingle) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name);
  }

}
