package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.enums.ScriptStatusPriority;
import com.project.graalrestservice.domain.models.ScriptInfo;

import java.time.LocalDateTime;

public class ScriptInfoForList implements Comparable<ScriptInfoForList>{
    private final String name;
    private final ScriptStatus status;
    private final LocalDateTime createdTime;
    private final String link;
    private final ScriptStatusPriority scriptStatusPriority;

    public ScriptInfoForList(String name, ScriptInfo scriptInfo, ScriptStatusPriority scriptStatusPriority) {
        this.name = name;
        this.status = scriptInfo.getScriptStatus();
        this.createdTime = scriptInfo.getCreateTime();
        this.link = scriptInfo.getLink();
        this.scriptStatusPriority = scriptStatusPriority;
    }

    public ScriptInfoForList(String name, ScriptInfo scriptInfo) {
        this(name, scriptInfo, new ScriptStatusPriority());
    }

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
    public String getLink() {
        return link;
    }
}
