package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

public class ScriptInfo implements StreamingResponseBody, Runnable {

    private final static Logger LOGGER = LogManager.getLogger(ScriptInfo.class);
    private final String name;
    private final String script;
    private volatile ScriptStatus status;
    private final String link;
    private final CircularOutputStream logStream;
    private final LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final Value value;
    private final Context context;
    private final ExecutorService executorService;
    private String outputInfo;

    public ScriptInfo(String name, String script, String link, CircularOutputStream logStream, Value value, Context context, ExecutorService executorService) {
        this.name = name;
        this.script = script;
        this.link = link;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = LocalDateTime.now();
        this.logStream = logStream;
        this.value = value;
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info(String.format("Attempting to run a script [%s]", name));
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
            LOGGER.info(String.format("Script [%s] execution completed successfully", name));
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
            LOGGER.info(String.format("Script [%s] execution failed. %s", name, e.getMessage()));
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

    public String getName() {
        return name;
    }
    public String getScript() {
        return script;
    }
    public synchronized ScriptStatus getScriptStatus() {
        return status;
    }
    public String getLink() {
        return link;
    }
    public String getOutputLogs() {
        return logStream.toString() + outputInfo;
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

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
            executorService.execute(this);
            while (getScriptStatus() == ScriptStatus.RUNNING || getScriptStatus() == ScriptStatus.IN_QUEUE || !logStream.isReadComplete()) {

                while (!logStream.isReadComplete()) {
                    outputStream.write(logStream.getCurrentByte());
                    outputStream.flush();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
    }

}
