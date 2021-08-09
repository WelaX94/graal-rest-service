package com.project.graalrestservice.domain.enums;

public enum ScriptStatus {
    IN_QUEUE(2),
    RUNNING(1),
    EXECUTION_SUCCESSFUL(0),
    EXECUTION_FAILED(0),
    EXECUTION_STOPPED(0);

    private int value;

    ScriptStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
