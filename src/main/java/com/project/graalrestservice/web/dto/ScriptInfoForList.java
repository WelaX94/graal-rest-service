package com.project.graalrestservice.web.dto;

import com.project.graalrestservice.web.controller.ScriptsController;
import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying partial information about the script in the list
 */
public class ScriptInfoForList extends RepresentationModel<ScriptInfoForList> {

  private String name;
  private ScriptStatus status;
  private Instant createTime;

  /**
   * Method for adding HATEOAS links.
   */
  public void setLinks() {
    add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(name)).withSelfRel());
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

  public String getName() {
    return name;
  }

  public ScriptStatus getStatus() {
    return status;
  }

  public String getCreateTime() {
    return createTime.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    ScriptInfoForList that = (ScriptInfoForList) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name);
  }
}
