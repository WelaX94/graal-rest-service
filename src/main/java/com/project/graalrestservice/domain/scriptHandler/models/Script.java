package com.project.graalrestservice.domain.scriptHandler.models;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.utils.CircularOutputStream;
import com.project.graalrestservice.domain.scriptHandler.utils.OutputStreamSplitter;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongScriptStatusException;
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
    private final OutputStream logStorageStream;
    private final OutputStreamSplitter streamSplitter;
    private final OffsetDateTime createTime;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Context context;

    public static Script create(String name, String scriptCode, int streamBufferCapacity) {
        validate(scriptCode);
        return new Script(name, scriptCode, streamBufferCapacity);
    }

    private static void validate(String scriptCode) {
        try (Context context = Context.create("js")){
            context.parse("js", scriptCode);
        } catch (PolyglotException e) {
            throw new WrongScriptException(e.getMessage());
        }
    }

    /**
     * Basic constructor
     * @param name            script name (identifier)
     * @param scriptCode      JS script
     */
    private Script(String name, String scriptCode, int streamBufferCapacity) {
        this.name = name;
        this.scriptCode = scriptCode;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = OffsetDateTime.now();
        this.logStorageStream = new CircularOutputStream(streamBufferCapacity);
        this.streamSplitter = new OutputStreamSplitter();
        this.streamSplitter.addStream(logStorageStream);
    }

    /**
     * Constructor required for tests
     */
    public Script(String name, String scriptCode, int streamBufferCapacity, ScriptStatus scriptStatus) {
        this(name, scriptCode, streamBufferCapacity);
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
            prepareScriptExecution();
            context.eval("js", scriptCode);
            processingSuccessfulExecution();
            logger.info(String.format("Script [%s] execution completed successfully", name));
        } catch (PolyglotException e) {
            processingFailedOrCanceledExecution(e);
            logger.info(String.format("Script [%s] execution failed. %s", name, e.getMessage()));
        }
    }

    private synchronized void prepareScriptExecution() {
        status = ScriptStatus.RUNNING;
        startTime = OffsetDateTime.now();
    }

    private synchronized void processingSuccessfulExecution() {
        endTime = OffsetDateTime.now();
        status = ScriptStatus.EXECUTION_SUCCESSFUL;
    }

    private synchronized void processingFailedOrCanceledExecution(PolyglotException e) {
        endTime = OffsetDateTime.now();
        if (e.isCancelled()) status = ScriptStatus.EXECUTION_CANCELED;
        else status = ScriptStatus.EXECUTION_FAILED;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        try {
            streamSplitter.write(sw.toString().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
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
    public synchronized ScriptStatus getStatus() {
        return status;
    }

    /**
     * A method to get the full output logs
     *
     * @return full output logs
     */
    public String getOutputLogs() {
        return logStorageStream.toString();
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

}
