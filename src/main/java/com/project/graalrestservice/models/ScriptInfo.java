package com.project.graalrestservice.models;

public class ScriptInfo extends Thread{

    final private String script;
    private ScriptInfo status;

    public ScriptInfo(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public ScriptInfo getStatus() {
        return status;
    }

    public void setStatus(ScriptInfo status) {
        this.status = status;
    }
}
