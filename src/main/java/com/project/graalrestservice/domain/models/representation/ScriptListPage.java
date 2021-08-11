package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.controller.ScriptsController;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ScriptListPage extends RepresentationModel<ScriptListPage> {

    private final int page;
    private final int pages;
    private final int totalScripts;
    private final List<ScriptInfoForList> scriptList;

    public ScriptListPage(List<ScriptInfoForList> scriptList, int pageNumber, int totalScripts, String filters) {
        this.scriptList = scriptList;
        this.totalScripts = totalScripts;
        this.page = pageNumber;
        this.pages = (totalScripts % 10 == 0) ? (totalScripts / 10) : (totalScripts / 10 + 1);
        if (pageNumber > 1) add(linkTo(methodOn(ScriptsController.class).getPageScripts(filters, pageNumber - 1)).withRel("previousPage"));
        if (page < pages) add(linkTo(methodOn(ScriptsController.class).getPageScripts(filters, pageNumber + 1)).withRel("nextPage"));
    }

    public int getTotalScripts() {
        return totalScripts;
    }
    public String getPage() {
        return String.format("%d of %d", page, pages);
    }
    public List<ScriptInfoForList> getScriptList() {
        return scriptList;
    }

}
