package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.controller.ScriptsController;
import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Helpful class for displaying partial information about the script in the list
 */
public class ScriptInfoForList extends RepresentationModel<ScriptInfoForList> implements Comparable<ScriptInfoForList>{

    private final static ScriptStatus.Priority defaultPriority = new ScriptStatus.Priority();
    private final String name;
    private final ScriptStatus status;
    private final LocalDateTime createdTime;
    private final ScriptStatus.Priority scriptStatusPriority;

    /**
     * Class constructor using custom script priority for sorting
     * @param name script name (identifier)
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
     * @param name script name (identifier)
     * @param scriptInfo the main scriptInfo containing all the information about the script
     * @see ScriptStatus.Priority
     */
    public ScriptInfoForList(String name, ScriptInfo scriptInfo) {
        this(scriptInfo, defaultPriority);
    }

    /**
     * The method calculates which of the two scripts is larger
     * @param script the script with which you want to compare the current
     * @return >0 if the current script is larger, <0 if the current script is smaller and 0 if they are equal
     */
    @Override
    public int compareTo(ScriptInfoForList script) {
        if (scriptStatusPriority.getPriority(this.status) == scriptStatusPriority.getPriority(script.getStatus())) {
            if (this.createdTime.isEqual(script.createdTime)) {
                return this.name.compareTo(script.name);
            }
            return script.createdTime.compareTo(this.createdTime);
        }
        return scriptStatusPriority.getPriority(this.status) - scriptStatusPriority.getPriority(script.getStatus());
    }

    public String getName() {
        return name;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

}
