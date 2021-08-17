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
     * @param list sheet to display
     * @param pageNumber desired page
     * @param totalScripts the total number of scripts that match the specified filters
     * @param filters filter list
     * @param pageSize page size
     */
    public Page(T list, Integer pageNumber, int totalScripts, String filters, Integer pageSize) {
        this.list = list;
        this.totalScripts = totalScripts;
        this.page = pageNumber;
        this.numPages = (totalScripts % pageSize == 0) ? (totalScripts / pageSize) : (totalScripts / pageSize + 1);
        this.scriptsOnPage = list.size();
        if (pageNumber > 1) add(linkTo(methodOn(ScriptsController.class).getScriptListPage(filters, pageSize, pageNumber - 1)).withRel("previousPage"));
        if (page < numPages) add(linkTo(methodOn(ScriptsController.class).getScriptListPage(filters, pageSize, pageNumber + 1)).withRel("nextPage"));
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
