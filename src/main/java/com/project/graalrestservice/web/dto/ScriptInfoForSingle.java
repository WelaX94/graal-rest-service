package com.project.graalrestservice.web.dto;

import com.project.graalrestservice.web.controller.ScriptsController;
import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying information about one script
 */
public class ScriptInfoForSingle extends RepresentationModel<ScriptInfoForSingle> {

  private String name;
  private ScriptStatus status;
  private Instant createTime;
  private Instant startTime;
  private Instant endTime;
  private int logsSize;

  /**
   * Method for adding HATEOAS links.
   */
  public void setLinks() {
    add(linkTo(methodOn(ScriptsController.class).getScriptLogs(this.name, null, null))
        .withRel("logs").expand());
    add(linkTo(methodOn(ScriptsController.class).getScriptCode(this.name)).withRel("script"));
    add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(this.name)).withSelfRel());
    add(linkTo(ScriptsController.class).withRel("scriptList"));
  }

  public String getName() {
    return this.name;
  }

  public ScriptStatus getStatus() {
    return this.status;
  }

  public String getCreateTime() {
    return this.createTime.toString();
  }

  public String getStartTime() {
    return (this.startTime == null) ? null : this.startTime.toString();
  }

  public String getEndTime() {
    return (this.endTime == null) ? null : this.endTime.toString();
  }

  public int getLogsSize() {
    return this.logsSize;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStatus(ScriptStatus status) {
    this.status = status;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(Instant endTime) {
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
    return Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.name);
  }

}
