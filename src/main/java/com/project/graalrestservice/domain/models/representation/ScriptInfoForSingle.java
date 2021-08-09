package com.project.graalrestservice.domain.models.representation;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;

import java.time.LocalDateTime;

public class ScriptInfoForSingle {

    private final String name;
    private final ScriptStatus status;
    private final LocalDateTime createTime;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String logs;

    public ScriptInfoForSingle(String name, ScriptInfo script) {
        this.name = name;
        this.status = script.getScriptStatus();
        this.createTime = script.getCreateTime();
        this.startTime = script.getStartTime();
        this.endTime = script.getEndTime();
        this.logs = script.getLink() + "/logs";
    }

    public String getName() {
        return name;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public String getLogs() {
        return logs;
    }

}
