package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;

public class ScriptInfo implements Runnable{

    private final String script;
    private volatile ScriptStatus status;
    private final String link;
    private final OutputStream logStream;
    private String outputInfo = "";
    private final LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final Value value;
    private final Context context;

    public ScriptInfo(String script, String link, OutputStream logStream, Value value, Context context) {
        this.script = script;
        this.link = link;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = LocalDateTime.now();
        this.logStream = logStream;
        this.value = value;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                status = ScriptStatus.RUNNING;
                startTime = LocalDateTime.now();
                logStream.write(startTime.toString().getBytes());
                logStream.write("\tAttempting to run a script\n".getBytes());
            }
            value.execute();
            synchronized (this) {
                endTime = LocalDateTime.now();
                status = ScriptStatus.EXECUTION_SUCCESSFUL;
                outputInfo = endTime + "\tExited in " + getExecutionTime() + "s.";
            }
        }
        catch (PolyglotException e) {
            synchronized (this) {
                endTime = LocalDateTime.now();
                if (e.isCancelled()) status = ScriptStatus.EXECUTION_CANCELED;
                else status = ScriptStatus.EXECUTION_FAILED;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                outputInfo += sw + endTime.toString() + " Exited in " + getExecutionTime() + "s.";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            context.close();
        }
    }

    public synchronized void stopScriptExecution(){
        if (status != ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("You cannot stop a script that is not running", status);
        else closeContext();
    }

    public String getScript() {
        return script;
    }
    public ScriptStatus getScriptStatus() {
        return status;
    }
    public String getLink() {
        return link;
    }
    public OutputStream getLogStream() {
        return logStream;
    }
    public String getOutputInfo() {
        return outputInfo;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void closeContext() {
        context.close(true);
    }

}
