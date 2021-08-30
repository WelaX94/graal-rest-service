package com.project.graalrestservice.web.dto;

import com.project.graalrestservice.web.controller.ScriptsController;
import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class to display a paginated list
 */
public class Page<T extends List<?>> extends RepresentationModel<Page<T>> {

  private final int pageNumber;
  private final int numPages;
  private final int totalScripts;
  private final int scriptsOnPage;
  private final T list;

  /**
   * Basic constructor
   * 
   * @param list list to display
   * @param pageNumber current page number
   * @param numPages total number of pages
   * @param totalScripts total scripts in list
   */
  public Page(T list, int pageNumber, int numPages, int totalScripts) {
    this.list = list;
    this.pageNumber = pageNumber;
    this.numPages = numPages;
    this.totalScripts = totalScripts;
    this.scriptsOnPage = list.size();
  }

  /**
   * Method for adding HATEOAS links. Parameters are needed to form a correct link
   */
  public void setLinks(int pageSize, ScriptStatus status, String nameContains, boolean orderByName,
      boolean reverseOrder) {
    if (this.pageNumber > 1)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(this.pageNumber - 1, pageSize,
          status, nameContains, orderByName, reverseOrder)).withRel("previousPage").expand());
    if (this.pageNumber < this.numPages)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(this.pageNumber + 1, pageSize,
          status, nameContains, orderByName, reverseOrder)).withRel("nextPage").expand());
  }

  public int getTotalScripts() {
    return this.totalScripts;
  }

  public int getScriptsOnPage() {
    return this.scriptsOnPage;
  }

  public int getPageNumber() {
    return this.pageNumber;
  }

  public int getNumPages() {
    return this.numPages;
  }

  public T getList() {
    return this.list;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Page<?> page = (Page<?>) o;
    return this.pageNumber == page.pageNumber && this.numPages == page.numPages
        && this.totalScripts == page.totalScripts && this.scriptsOnPage == page.scriptsOnPage
        && Objects.equals(this.list, page.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.pageNumber, this.numPages, this.totalScripts,
        this.scriptsOnPage, this.list);
  }

}
