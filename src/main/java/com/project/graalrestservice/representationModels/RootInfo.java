package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.Controller;
import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * A class for outputting single messages from root controller in JSON format
 */
public class RootInfo extends RepresentationModel<RootInfo> {

  private final String info;

  public RootInfo(String info) {
    this.info = info;
    add(linkTo(Controller.class).withSelfRel());
    add(linkTo(ScriptsController.class).withRel("scriptList"));
  }

  public String getInfo() {
    return info;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    RootInfo rootInfo = (RootInfo) o;
    return Objects.equals(info, rootInfo.info);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), info);
  }
}
