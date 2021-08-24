package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class to display a list of scripts
 */
public class Page<T extends List<?>> extends RepresentationModel<Page<T>> {

  private final int page;
  private final int numPages;
  private final int totalScripts;
  private final int scriptsOnPage;
  private final T list;

  /**
   * Basic constructor
   * 
   * @param list sheet to display
   */
  public Page(T list, int page, int numPages, int totalScripts) {
    this.list = list;
    this.page = page;
    this.numPages = numPages;
    this.totalScripts = totalScripts;
    this.scriptsOnPage = list.size();
  }

  public void setLinks(int pageSize, String status, String nameContains, boolean orderByName,
      boolean reverseOrder) {
    if (page > 1)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(page - 1, pageSize, status,
          nameContains, orderByName, reverseOrder)).withRel("previousPage").expand());
    if (page < numPages)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(page + 1, pageSize, status,
          nameContains, orderByName, reverseOrder)).withRel("nextPage").expand());
  }

  public int getTotalScripts() {
    return totalScripts;
  }

  public int getScriptsOnPage() {
    return scriptsOnPage;
  }

  public int getPage() {
    return page;
  }

  public int getNumPages() {
    return numPages;
  }

  public T getList() {
    return list;
  }

}
