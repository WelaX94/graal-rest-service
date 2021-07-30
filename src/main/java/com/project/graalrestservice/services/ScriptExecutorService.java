package com.project.graalrestservice.services;

import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptExecutor;
import org.graalvm.polyglot.Context;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptExecutorService implements ScriptExecutor {

    public void execute(String script) {

    }
}

class ScriptExecutionThread extends Thread {

    final ScriptInfo scriptInfo;

    ScriptExecutionThread(ScriptInfo scriptInfo) {
        this.scriptInfo = scriptInfo;
    }

    @Override
    public void run() {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        String result = "";

        try (Context context = Context.create()){
            context.eval("js", scriptInfo.getScript());
        }
        catch (Exception e) {
            result = e.getMessage();
        }
        finally {
            System.out.flush();
            System.setOut(old);
        }
        result += baos.toString();
    }

}
