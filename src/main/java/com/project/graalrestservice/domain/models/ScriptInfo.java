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

/**
 * A class that contains all the information about the script, as well as the code to run it
 */
public class ScriptInfo implements StreamingResponseBody, Runnable {

    private final static Logger LOGGER = LogManager.getLogger(ScriptInfo.class);
    private final String name;
    private final String script;
    private volatile ScriptStatus status;
    private final String logsLink;
    private final CircularOutputStream logStream;
    private final LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final Value value;
    private final Context context;
    private final ExecutorService executorService;
    private String inputInfo;
    private String outputInfo;

    /**
     * Basic constructor
     * @param name script name (identifier)
     * @param script JS script
     * @param logsLink link for script output logs
     * @param logStream stream to record logs
     * @param value Value used to run the script processing
     * @param context Context, which handles the script
     * @param executorService service to start a new thread
     */
    public ScriptInfo(String name, String script, String logsLink, CircularOutputStream logStream, Value value, Context context, ExecutorService executorService) {
        this.name = name;
        this.script = script;
        this.logsLink = logsLink;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = LocalDateTime.now();
        this.logStream = logStream;
        this.value = value;
        this.context = context;
        this.executorService = executorService;
        this.inputInfo = String.format("%s\tScript created and added to the execution queue\n", createTime);
    }

    /**
     * The method used to run the script processing. Can be executed asynchronously by passing the current object to the Executor Service
     */
    @Override
    public void run() {
        try {
            LOGGER.info(String.format("Attempting to run a script [%s]", name));
            synchronized (this) {
                status = ScriptStatus.RUNNING;
                startTime = LocalDateTime.now();
                inputInfo += startTime + "\tAttempting to run a script\n";
            }
            value.execute();
            synchronized (this) {
                endTime = LocalDateTime.now();
                status = ScriptStatus.EXECUTION_SUCCESSFUL;
                outputInfo = endTime + "\tExited in " + getExecutionTime() + "s.\n";
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
                outputInfo += sw + endTime.toString() + " Exited in " + getExecutionTime() + "s.\n";
            }
            LOGGER.info(String.format("Script [%s] execution failed. %s", name, e.getMessage()));
        } finally {
            context.close();
        }
    }

    /**
     * The method needed to stop the script
     */
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
    public String getLogsLink() {
        return logsLink;
    }
    public String getOutputLogs() {
        if (outputInfo == null) return inputInfo + logStream.toString();
        else return inputInfo + logStream.toString() + outputInfo;
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

    /**
     * The method required to run a script with the ability to stream logs in real time
     * @param outputStream stream, through which the logs will be streamed
     * @throws IOException
     */
    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(inputInfo.getBytes());
        outputStream.flush();
        executorService.execute(this);
        while (getScriptStatus() == ScriptStatus.IN_QUEUE) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        outputStream.write(String.format("%s\tAttempting to run a script\n", startTime).getBytes());
        outputStream.flush();
        while (getScriptStatus() == ScriptStatus.RUNNING || !logStream.isReadComplete()) {
            while (!logStream.isReadComplete()) {
                outputStream.write(logStream.getNextByte());
                outputStream.flush();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        outputStream.write(outputInfo.getBytes());
        outputStream.flush();
    }

}
