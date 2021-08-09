package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;

import java.time.LocalDateTime;

public class ScriptInfoForList implements Comparable<ScriptInfoForList>{
    private String name;
    private ScriptStatus status;
    private LocalDateTime createdTime;
    private String link;

    public ScriptInfoForList(String name, ScriptInfo scriptInfo) {
        this.name = name;
        this.status = scriptInfo.getScriptStatus();
        this.createdTime = scriptInfo.getCreateTime();
        this.link = scriptInfo.getLink();
    }

    @Override
    public int compareTo(ScriptInfoForList script) {
        if (this.status.getValue() == script.status.getValue()) {
            if (this.createdTime.isEqual(script.createdTime)) {
                return this.name.compareTo(script.name);
            }
            return script.createdTime.compareTo(this.createdTime);
        }
        return this.status.getValue() - script.status.getValue();
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
