package com.project.graalrestservice.representationModels; // NOSONAR

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
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
    if (pageNumber > 1)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(pageNumber - 1, pageSize,
          status, nameContains, orderByName, reverseOrder)).withRel("previousPage").expand());
    if (pageNumber < numPages)
      add(linkTo(methodOn(ScriptsController.class).getScriptListPage(pageNumber + 1, pageSize,
          status, nameContains, orderByName, reverseOrder)).withRel("nextPage").expand());
  }

  public int getTotalScripts() {
    return totalScripts;
  }

  public int getScriptsOnPage() {
    return scriptsOnPage;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public int getNumPages() {
    return numPages;
  }

  public T getList() {
    return list;
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
    return pageNumber == page.pageNumber && numPages == page.numPages
        && totalScripts == page.totalScripts && scriptsOnPage == page.scriptsOnPage
        && Objects.equals(list, page.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pageNumber, numPages, totalScripts, scriptsOnPage, list);
  }
}
