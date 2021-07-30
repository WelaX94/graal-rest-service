package com.project.graalrestservice.models;

import com.project.graalrestservice.enums.ScriptStatus;
import org.graalvm.polyglot.Context;

public class ScriptInfo {

    final private String script;
    private ScriptStatus status;
    final private String link;
    private String log = "";
    private Context context;

    public ScriptInfo(String scriptName, String script) {
        this.script = script;
        this.link = "http://localhost:3030/scripts/" + scriptName;
        this.status = ScriptStatus.IN_QUEUE;
    }

    public String getScript() {
        return script;
    }
    public ScriptStatus getStatus() {
        return status;
    }
    public void setStatus(ScriptStatus status) {
        this.status = status;
    }
    public void ScriptStatus(ScriptStatus status) {
        this.status = status;
    }
    public String getLog() {
        return log;
    }
    public void addLog(String log) {
        this.log += log;
    }
    public String getLink() {
        return link;
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
