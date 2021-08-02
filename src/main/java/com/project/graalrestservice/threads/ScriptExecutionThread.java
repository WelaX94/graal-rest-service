package com.project.graalrestservice.threads;

import com.project.graalrestservice.enums.ScriptStatus;
import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.util.CircularOutputStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.IOException;
import java.io.OutputStream;


public class ScriptExecutionThread extends Thread {

    final ScriptInfo scriptInfo;

    public ScriptExecutionThread(ScriptInfo scriptInfo) {
        this.scriptInfo = scriptInfo;
    }

    @Override
    public void run() {

        scriptInfo.setScriptStatus(ScriptStatus.RUNNING);
        OutputStream outputStream = new CircularOutputStream(65536);
        scriptInfo.setLogStream(outputStream);
        Context context = Context.newBuilder().out(outputStream).build();
        scriptInfo.setContext(context);
        try (context){
            outputStream.write("Attempting to run a script\n".getBytes());
            context.eval("js", scriptInfo.getScript());
            scriptInfo.setScriptStatus(ScriptStatus.EXECUTION_SUCCESSFUL);
        }
        catch (PolyglotException e) {
            if (e.getMessage().equals("Context execution was cancelled.")) scriptInfo.setScriptStatus(ScriptStatus.EXECUTION_STOPPED);
            else scriptInfo.setScriptStatus(ScriptStatus.EXECUTION_FAILED);
            scriptInfo.setError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}