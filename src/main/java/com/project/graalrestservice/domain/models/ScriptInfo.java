package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;

public class ScriptInfo implements Runnable{

    final private String script;
    private ScriptStatus status;
    final private String link;
    private OutputStream logStream;
    private Context context;
    private String outputInfo = "";
    private final LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScriptInfo(String script, String link) {
        this.script = script;
        this.link = link;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = LocalDateTime.now();
    }

    @Override
    public void run() {
        status = ScriptStatus.RUNNING;
        logStream = new CircularOutputStream(65536);
        context = Context.newBuilder().out(logStream).err(logStream).build();
        try {
            logStream.write("Attempting to run a script\n".getBytes());
            startTime = LocalDateTime.now();
            context.eval("js", getScript());
            endTime = LocalDateTime.now();
            status = ScriptStatus.EXECUTION_SUCCESSFUL;
            outputInfo = "Exited in " + getExecutionTime() + "s.";
        }
        catch (PolyglotException e) {
            endTime = LocalDateTime.now();
            if (e.isCancelled()) status = ScriptStatus.EXECUTION_STOPPED;
            else status = ScriptStatus.EXECUTION_FAILED;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            outputInfo += sw + "Exited in " + getExecutionTime() + "s.";
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            context.close();
        }
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
    public String getOutputInfo() {
        return outputInfo;
    }
    public void setOutputInfo(String outputInfo) {
        this.outputInfo = outputInfo;
    }
    public String getExecutionTime(){
        Duration duration = Duration.between(startTime, endTime);
        return String.format("%d.%03d", duration.getSeconds(), (duration.getNano() / 1_000_000));
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
