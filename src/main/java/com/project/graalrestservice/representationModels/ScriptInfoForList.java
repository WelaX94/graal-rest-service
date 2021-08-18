package com.project.graalrestservice.representationModels;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
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
     * @param scriptInfo the main scriptInfo containing all the information about the script
     * @param scriptStatusPriority custom script priority for sorting
     * @see ScriptStatus.Priority
     */
    public ScriptInfoForList(ScriptInfo scriptInfo, ScriptStatus.Priority scriptStatusPriority) {
        this.name = scriptInfo.getName();
        this.status = scriptInfo.getScriptStatus();
        this.createdTime = scriptInfo.getCreateTime();
        this.scriptStatusPriority = scriptStatusPriority;
        add(linkTo(methodOn(ScriptsController.class).getSingleScriptInfo(name)).withSelfRel());
    }

    /**
     * Class constructor using default script priority for sorting
     * @param scriptInfo the main scriptInfo containing all the information about the script
     * @see ScriptStatus.Priority
     */
    public ScriptInfoForList(ScriptInfo scriptInfo) {
        this(scriptInfo, defaultPriority);
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
