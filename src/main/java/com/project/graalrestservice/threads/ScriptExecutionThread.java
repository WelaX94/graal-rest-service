package com.project.graalrestservice.threads;

import com.project.graalrestservice.enums.ScriptStatus;
import com.project.graalrestservice.models.ScriptInfo;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptExecutionThread extends Thread {

    final ScriptInfo scriptInfo;

    public ScriptExecutionThread(ScriptInfo scriptInfo) {
        this.scriptInfo = scriptInfo;
    }

    @Override
    public void run() {
        scriptInfo.setStatus(ScriptStatus.RUNNING);
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        Context context = Context.create();
        scriptInfo.setContext(context);
        String errorMessage = "";
        try (context){
            context.eval("js", scriptInfo.getScript());
            scriptInfo.setStatus(ScriptStatus.EXECUTION_SUCCESSFUL);
        }
        catch (PolyglotException e) {
            if (e.getMessage().equals("Context execution was cancelled.")) scriptInfo.setStatus(ScriptStatus.EXECUTION_STOPPED);
            else scriptInfo.setStatus(ScriptStatus.EXECUTION_FAILED);
            errorMessage = e.getMessage();
        }
        finally {
            System.out.flush();
            System.setOut(old);
        }
        scriptInfo.addLog(baos + errorMessage);
    }

}