package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * A class that contains all the information about the script, as well as the code to run it
 */
public class ScriptInfo implements StreamingResponseBody, Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ScriptInfo.class);
    private final String name;
    private final String script;
    private volatile ScriptStatus status;
    private final String logsLink;
    private final CircularOutputStream logStream;
    private final OffsetDateTime createTime;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private final Value value;
    private final Context context;
    private String inputInfo;
    private String outputInfo;

    /**
     * Basic constructor
     *
     * @param name            script name (identifier)
     * @param script          JS script
     * @param logsLink        link for script output logs
     * @param logStream       stream to record logs
     * @param value           Value used to run the script processing
     * @param context         Context, which handles the script
     */
    public ScriptInfo(
            String name, String script, String logsLink,
            CircularOutputStream logStream, Value value, Context context) {
        this.name = name;
        this.script = script;
        this.logsLink = logsLink;
        this.status = ScriptStatus.IN_QUEUE;
        this.createTime = OffsetDateTime.now();
        this.logStream = logStream;
        this.value = value;
        this.context = context;
        this.inputInfo = String.format("%s\tScript created and added to the execution queue\n", createTime);
    }

    /**
     * Constructor required for tests
     */
    public ScriptInfo(
            String name, String script, String logsLink,
            CircularOutputStream logStream, Value value, Context context,
            ScriptStatus scriptStatus) {
        this(name, script, logsLink, logStream, value, context);
        this.status = scriptStatus;
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
                startTime = OffsetDateTime.now();
                inputInfo += startTime + "\tAttempting to run a script\n";
            }
            value.execute();
            synchronized (this) {
                endTime = OffsetDateTime.now();
                status = ScriptStatus.EXECUTION_SUCCESSFUL;
                outputInfo = endTime + "\tExited in " + getExecutionTime() + "s.\n";
            }
            LOGGER.info(String.format("Script [%s] execution completed successfully", name));
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
            LOGGER.info(String.format("Script [%s] execution failed. %s", name, e.getMessage()));
        } finally {
            context.close();
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
     * The method required to run a script with the ability to stream logs in real time
     *
     * @param outputStream stream, through which the logs will be streamed
     * @throws IOException
     */
    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        try {
            outputStream.write(inputInfo.getBytes());
            outputStream.flush();
            while (getScriptStatus() == ScriptStatus.IN_QUEUE) {
                Thread.sleep(100);
            }
            outputStream.write(String.format("%s\tAttempting to run a script\n", startTime).getBytes());
            outputStream.flush();
            while (getScriptStatus() == ScriptStatus.RUNNING || !logStream.isReadComplete()) {
                outputStream.write(logStream.getNextBytes());
                outputStream.flush();
            }
            outputStream.write(outputInfo.getBytes());
            outputStream.flush();
        } catch (ClientAbortException | InterruptedException e) {
            LOGGER.error("Client connection breakage. The script continues its work. " + e.getMessage());
        } finally {
            logStream.disableRealTimeReading();
        }
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
        if (outputInfo == null) return inputInfo + logStream.toString();
        else return inputInfo + logStream.toString() + outputInfo;
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

    public String getName() {
        return name;
    }
    public String getScript() {
        return script;
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

}
