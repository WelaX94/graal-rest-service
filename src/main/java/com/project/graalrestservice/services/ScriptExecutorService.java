package com.project.graalrestservice.services;

import com.project.graalrestservice.repositories.ScriptExecutor;
import org.graalvm.polyglot.Context;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptExecutorService implements ScriptExecutor {

    public String execute(String script) {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        String result;

        try (Context context = Context.create()){
            context.eval("js", script);
            result = "Script execution success.\n";
        }
        catch (Exception e) {
            result = "Script execution failed.\n" + e.getMessage();
        }
        finally {
            System.out.flush();
            System.setOut(old);
        }
        return result + baos.toString();
    }
}
