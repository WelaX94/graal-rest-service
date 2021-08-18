package com.project.graalrestservice.domain.scriptHandler.models;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.utils.CircularOutputStream;
import com.project.graalrestservice.domain.scriptHandler.utils.OutputStreamSplitter;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.*;
import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * A class that contains all the information about the script, as well as the code to run it
 */
public class Script implements Runnable {

    private static final Logger logger = LogManager.getLogger(Script.class);
    private final String name;
    private final String scriptCode;
    private volatile ScriptStatus status;
    private final String logsLink;
    private final OutputStream logStorageStream;
    private final OutputStreamSplitter streamSplitter;
    private final OffsetDateTime createTime;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Context context;
    private String inputInfo;
    private String outputInfo;


    /**
     * Basic constructor
     *
     * @param name            script name (identifier)
     * @param scriptCode          JS script
     * @param logsLink        link for script output logs
     */
    public Script(
            String name, String scriptCode, String logsLink, int streamBufferCapacity) {
        this.name = name;
        this.scriptCode = scriptCode;
        this.logsLink = logsLink;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = OffsetDateTime.now();
        this.inputInfo = String.format("%s\tScript created and added to the execution queue\n", createTime);
        this.logStorageStream = new CircularOutputStream(streamBufferCapacity);
        this.streamSplitter = new OutputStreamSplitter();
        this.streamSplitter.addStream(logStorageStream);
    }

    /**
     * Constructor required for tests
     */
    public Script(
            String name, String scriptCode, String logsLink,
            int streamBufferCapacity, ScriptStatus scriptStatus) {
        this(name, scriptCode, logsLink, streamBufferCapacity);
        this.status = scriptStatus;
    }

    /**
     * The method used to run the script processing. Can be executed asynchronously by passing the current object to the Executor Service
     */
    @Override
    public void run() {
        try (Context context = Context.newBuilder().out(streamSplitter).err(streamSplitter).allowCreateThread(true).build()){
            this.context = context;
            logger.info(String.format("Attempting to run a script [%s]", name));
            synchronized (this) {
                status = ScriptStatus.RUNNING;
                startTime = OffsetDateTime.now();
                inputInfo += startTime + "\tAttempting to run a script\n";
            }
            context.eval("js", scriptCode);
            synchronized (this) {
                endTime = OffsetDateTime.now();
                status = ScriptStatus.EXECUTION_SUCCESSFUL;
                outputInfo = endTime + "\tExited in " + getExecutionTime() + "s.\n";
            }
            logger.info(String.format("Script [%s] execution completed successfully", name));
        } catch (PolyglotException e) {
            synchronized (this) {
                endTime = OffsetDateTime.now();
                if (e.isCancelled()) status = ScriptStatus.EXECUTION_CANCELED;
                else status = ScriptStatus.EXECUTION_FAILED;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                outputInfo += sw + endTime.toString() + " Exited in " + getExecutionTime() + "s.\n";
            }
            logger.info(String.format("Script [%s] execution failed. %s", name, e.getMessage()));
        }
    }

    /**
     * The method needed to stop the script
     */
    public synchronized void stopScriptExecution() {
        if (status != ScriptStatus.RUNNING) throw new WrongScriptStatusException
                ("You cannot stop a script that is not running", status);
        else closeContext();
    }

    /**
     * A method to get the current status of the script
     *
     * @return current status of the script
     */
    public synchronized ScriptStatus getScriptStatus() {
        return status;
    }

    /**
     * A method to get the full output logs
     *
     * @return full output logs
     */
    public String getOutputLogs() {
        if (outputInfo == null) return inputInfo + logStorageStream.toString();
        else return inputInfo + logStorageStream.toString() + outputInfo;
    }

    /**
     * A method to get the execution time of the script
     *
     * @return execution time of the script
     */
    public String getExecutionTime() {
        Duration duration = Duration.between(startTime, endTime);
        return String.format("%d.%03d", duration.getSeconds(), (duration.getNano() / 1_000_000));
    }

    /**
     * Method for closing the context
     */
    public void closeContext() {
        context.close(true);
    }

    public void addStreamForRecording(OutputStream outputStream) {
        streamSplitter.addStream(outputStream);
    }
    public void deleteStreamForRecording(OutputStream outputStream) {
        streamSplitter.deleteStream(outputStream);
    }


    public String getName() {
        return name;
    }
    public String getScriptCode() {
        return scriptCode;
    }
    public String getLogsLink() {
        return logsLink;
    }
    public OffsetDateTime getCreateTime() {
        return createTime;
    }
    public OffsetDateTime getStartTime() {
        return startTime;
    }
    public OffsetDateTime getEndTime() {
        return endTime;
    }
    public OutputStream getLogStorageStream() {
        return logStorageStream;
    }
    public String getInputInfo() {
        return inputInfo;
    }
    public String getOutputInfo() {
        return outputInfo;
    }

}
