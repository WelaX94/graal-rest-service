package com.project.graalrestservice.domain.enums;

public enum ScriptStatus {
    IN_QUEUE('q'),
    RUNNING('r'),
    EXECUTION_SUCCESSFUL('s'),
    EXECUTION_FAILED('f'),
    EXECUTION_CANCELED('c');

    private final char letter;

    ScriptStatus(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

}
