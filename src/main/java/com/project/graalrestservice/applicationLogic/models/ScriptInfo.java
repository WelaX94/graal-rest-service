package com.project.graalrestservice.applicationLogic.models;

import com.project.graalrestservice.applicationLogic.enums.ScriptStatus;
import org.graalvm.polyglot.Context;

import java.io.OutputStream;

public class ScriptInfo {

    final private String script;
    private ScriptStatus status;
    final private String link;
    private OutputStream logStream;
    private Context context;
    private String error = "";

    public ScriptInfo(String scriptName, String script, String host, int port) {
        this.script = script;
        this.link = host + ":" + port + "/scripts/" + scriptName;
        this.status = ScriptStatus.IN_QUEUE;
    }

    public String getScript() {
        return script;
    }
    public ScriptStatus getScriptStatus() {
        return status;
    }
    public void setScriptStatus(ScriptStatus status) {
        this.status = status;
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
    public OutputStream getLogStream() {
        return logStream;
    }
    public void setLogStream(OutputStream logStream) {
        this.logStream = logStream;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}