package com.project.graalrestservice.domain.script.model;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import com.project.graalrestservice.domain.script.exception.ScriptNotFoundException;
import com.project.graalrestservice.domain.script.util.CircularOutputStream;
import com.project.graalrestservice.domain.script.util.OutputStreamSplitter;
import com.project.graalrestservice.domain.script.exception.WrongScriptException;
import com.project.graalrestservice.domain.script.exception.WrongScriptStatusException;
import com.project.graalrestservice.web.controller.ScriptsController;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.time.Instant;

import static com.project.graalrestservice.domain.script.enumeration.ScriptStatus.*;

/**
 * A class that contains all the information about the script, as well as the code to run it
 */
public class Script implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Script.class);
  private static final String MDC_NAME_IDENTIFIER = "scriptName";
  private final String name;
  private final String scriptCode;
  private volatile ScriptStatus status;
  /**
   * The {@link CircularOutputStream} is used to store logs. If its size is exceeded, the data will
   * be overwritten according to the Circular buffer principle
   */
  private final OutputStream logStorageStream;
  /**
   * {@link OutputStreamSplitter} is needed to divide one OS into several independent of each other.
   * In this case, this functionality is needed to be able to save logs and simultaneously
   * {@link ScriptsController#runScriptWithLogsStreaming(String, String) stream them}.
   */
  private final OutputStreamSplitter mainStream;
  private final Instant createTime;
  private Instant startTime;
  private Instant endTime;
  private Context context;

  /**
   * The Script constructor is {@link #Script(String, String, int) private} and this method is used
   * to create objects. It first {@link #validate(String) checks the validity} of the script, and if
   * all is well, it creates an {@link Script} object and returns it.
   * 
   * @param name script name (identifier)
   * @param scriptCode JS body
   * @param streamBufferCapacity the size of the OutputStream in which the logs will be stored
   * @return Script object
   */
  public static Script create(String name, String scriptCode, int streamBufferCapacity) {
    validate(scriptCode);
    return new Script(name, scriptCode, streamBufferCapacity);
  }

  /**
   * The method is used to check the validity of a script before creating a Script object. To do
   * this, a {@link Context context} is created and an attempt is made to parse the script. On error
   * an {@link WrongScriptException exception} is thrown
   * 
   * @param scriptCode JS body
   * @throws WrongScriptException if the script failed to parse
   */
  private static void validate(String scriptCode) {
    try (Context jsContext = Context.create("js")) {
      jsContext.parse("js", scriptCode);
      logger.trace("[{} - Validation of the script was successful]", MDC.get(MDC_NAME_IDENTIFIER));
    } catch (PolyglotException e) {
      logger.debug("[{} - Failed to validate the script]", MDC.get(MDC_NAME_IDENTIFIER));
      throw new WrongScriptException(e.getMessage());
    }
  }

  /**
   * Private constructor. To create an object you must use {@link #create(String, String, int) this
   * method}
   */
  private Script(String name, String scriptCode, int streamBufferCapacity) {
    this.name = name;
    this.scriptCode = scriptCode;
    this.status = IN_QUEUE;
    this.createTime = Instant.now();
    this.logStorageStream = new CircularOutputStream(streamBufferCapacity);
    this.mainStream = new OutputStreamSplitter();
    this.mainStream.addStream(logStorageStream);
    logger.trace("[{}] - Script object created]", name);
  }

  /**
   * The method is used to start processing the script. {@link #prepareScriptExecution() Preparing}
   * to run, its {@link #processingSuccessfulExecution() successful} or
   * {@link #processingFailedOrCanceledExecution(PolyglotException) unsuccessful} execution is
   * performed by the corresponding methods. {@link OutputStreamSplitter} used as a base streamer
   * for log processing. It is needed to be able to save logs and simultaneously
   * {@link ScriptsController#runScriptWithLogsStreaming(String, String) stream them}.
   */
  @Override
  public void run() {
    try (Context jsContext =
        Context.newBuilder().out(mainStream).err(mainStream).allowCreateThread(true).build()) {
      this.context = jsContext;
      prepareScriptExecution();
      jsContext.eval("js", this.scriptCode);
      processingSuccessfulExecution();
      logger.info("[{}] - Execution completed successfully", this.name);
    } catch (PolyglotException e) {
      processingFailedOrCanceledExecution(e);
      logger.info("[{}] - Execution failed. {}", this.name, e.getMessage());
    } catch (ScriptNotFoundException e) {
      logger.info(
          "[{}] - The script has been removed from the repository. The run has been cancelled",
          this.name);
    }
  }

  /**
   * The method performs preparatory actions before {@link #run() running} the script
   */
  private synchronized void prepareScriptExecution() {
    logger.info("[{}] - Attempting to run a script", this.name);
    MDC.put(MDC_NAME_IDENTIFIER, this.name);
    if (this.status == EXECUTION_CANCELED)
      throw new ScriptNotFoundException("Script was deleted from repository");
    this.status = RUNNING;
    this.startTime = Instant.now();
  }

  /**
   * The method handles the successful completion of the script {@link #run() execution}
   */
  private synchronized void processingSuccessfulExecution() {
    this.endTime = Instant.now();
    this.status = EXECUTION_SUCCESSFUL;
    logger.trace("[{}] - Processing of successful completion of the script is finished", this.name);
  }

  /**
   * The method handles unsuccessful script {@link #run() execution} (including
   * {@link #stopScriptExecution() stopping it forcibly}).
   * 
   * @param e PolyglotException, which stores the cause of the failed execution
   */
  private synchronized void processingFailedOrCanceledExecution(PolyglotException e) {
    this.endTime = Instant.now();
    if (e.isCancelled())
      status = EXECUTION_CANCELED;
    else
      status = EXECUTION_FAILED;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    try {
      this.mainStream.write(sw.toString().getBytes());
    } catch (IOException ex) {
      logger.error("[{}] - error writing exception stack trace to log stream", this.name);
      ex.printStackTrace();
    }
    logger.trace("[{}] - Processing of failed completion of the script is finished", this.name);
  }

  /**
   * The method needed to stop the script. You can only stop a script with the status RUNNING.
   * Otherwise, an {@link WrongScriptStatusException exception} will be thrown
   * 
   * @throws WrongScriptException if the script status is not RUNNING
   */
  public synchronized void stopScriptExecution() {
    if (this.status != RUNNING)
      throw new WrongScriptStatusException("You cannot stop a script that is not running",
          this.status);
    else
      this.context.close(true);
    logger.trace("[{}] - Script execution stopped", this.name);
  }

  /**
   * A method to get the current status of the script
   * 
   * @return current status of the script
   */
  public synchronized ScriptStatus getStatus() {
    return this.status;
  }

  /**
   * A method to get the output logs
   * 
   * @return output logs
   */
  public String getOutputLogs() {
    return this.logStorageStream.toString();
  }

  /**
   * Adds another stream to {@link #mainStream}
   * 
   * @param outputStream OutputStream
   */
  public void addStreamForRecording(OutputStream outputStream) {
    this.mainStream.addStream(outputStream);
  }

  /**
   * Deletes stream from {@link #mainStream}
   * 
   * @param outputStream OutputStream for removal
   */
  public void deleteStreamForRecording(OutputStream outputStream) {
    this.mainStream.deleteStream(outputStream);
  }

  /**
   * This method works if the script was deleted while it was in the execution queue
   */
  public synchronized void cancelExecution() {
    this.status = EXECUTION_CANCELED;
    if (this.context != null)
      context.close(true);
  }

  public int getLogsSize() {
    return this.logStorageStream.toString().length();
  }

  public String getName() {
    return this.name;
  }

  public String getScriptCode() {
    return this.scriptCode;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public Instant getStartTime() {
    return this.startTime;
  }

  public Instant getEndTime() {
    return this.endTime;
  }

}
