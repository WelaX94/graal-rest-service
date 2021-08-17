package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class to display a list of scripts
 */
public class ScriptListPage extends RepresentationModel<ScriptListPage> {

    private final int page;
    private final int pages;
    private final int totalScripts;
    private final int scriptsOnPage;
    private final List<ScriptInfoForList> scriptList;

    /**
     * Basic constructor
     * @param scriptList script sheet to display
     * @param pageNumber desired page
     * @param totalScripts the total number of scripts that match the specified filters
     * @param filters filter list
     * @param pageSize page size
     */
    public ScriptListPage(List<ScriptInfoForList> scriptList, Integer pageNumber, int totalScripts, String filters, Integer pageSize) {
        this.scriptList = scriptList;
        this.totalScripts = totalScripts;
        this.page = pageNumber;
        this.pages = (totalScripts % pageSize == 0) ? (totalScripts / pageSize) : (totalScripts / pageSize + 1);
        this.scriptsOnPage = scriptList.size();
        if (pageNumber > 1) add(linkTo(methodOn(ScriptsController.class).getScriptListPage(filters, pageSize, pageNumber - 1)).withRel("previousPage"));
        if (page < pages) add(linkTo(methodOn(ScriptsController.class).getScriptListPage(filters, pageSize, pageNumber + 1)).withRel("nextPage"));
    }

    public int getTotalScripts() {
        return totalScripts;
    }
    public int getScriptsOnPage() {
        return scriptsOnPage;
    }
    public String getPage() {
        return String.format("%d of %d", page, pages);
    }
    public List<ScriptInfoForList> getScriptList() {
        return scriptList;
    }

}
