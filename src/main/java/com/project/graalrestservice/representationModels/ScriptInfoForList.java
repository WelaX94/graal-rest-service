package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import org.springframework.hateoas.RepresentationModel;

import java.time.OffsetDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying partial information about the script in the list
 */
public class ScriptInfoForList extends RepresentationModel<ScriptInfoForList> {

    private static final ScriptStatus.Priority defaultPriority = new ScriptStatus.Priority();
    private final String name;
    private final ScriptStatus status;
    private final OffsetDateTime createdTime;
    private final ScriptStatus.Priority scriptStatusPriority;

    /**
     * Class constructor using custom script priority for sorting
     * @param script the main script containing all the information about the script
     * @param scriptStatusPriority custom script priority for sorting
     * @see ScriptStatus.Priority
     */
    public ScriptInfoForList(Script script, ScriptStatus.Priority scriptStatusPriority) {
        this.name = script.getName();
        this.status = script.getStatus();
        this.createdTime = script.getCreateTime();
        this.scriptStatusPriority = scriptStatusPriority;
        add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(name)).withSelfRel());
    }

    /**
     * Class constructor using default script priority for sorting
     * @param script the main script containing all the information about the script
     * @see ScriptStatus.Priority
     */
    public ScriptInfoForList(Script script) {
        this(script, defaultPriority);
    }

    public String getName() {
        return name;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public String getCreatedTime() {
        return createdTime.toString();
    }
    public int returnPriority() {
        return scriptStatusPriority.getPriority(status);
    }

}
