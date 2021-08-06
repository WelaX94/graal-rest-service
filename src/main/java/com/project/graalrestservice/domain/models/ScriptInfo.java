package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ScriptInfo implements Runnable{

    final private String script;
    private ScriptStatus status;
    final private String link;
    private OutputStream logStream;
    private Context context;
    private String outputInfo = "";

    public ScriptInfo(String script, String link) {
        this.script = script;
        this.link = link;
        this.status = ScriptStatus.IN_QUEUE;
    }

    @Override
    public void run() {
        setScriptStatus(ScriptStatus.RUNNING);
        OutputStream outputStream = new CircularOutputStream(65536);
        setLogStream(outputStream);
        Context context = Context.newBuilder().out(outputStream).err(outputStream).build();
        setContext(context);
        try (context){
            outputStream.write("Attempting to run a script\n".getBytes());
            context.eval("js", getScript());
            setScriptStatus(ScriptStatus.EXECUTION_SUCCESSFUL);
            outputInfo = "Exited in /TIME/";
        }
        catch (PolyglotException e) {
            if (e.isCancelled()) setScriptStatus(ScriptStatus.EXECUTION_STOPPED);
            else setScriptStatus(ScriptStatus.EXECUTION_FAILED);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            outputInfo += sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getScript() {
        return script;
    }
    public ScriptStatus getScriptStatus() {
        return status;
    }
    public void setScriptStatus(ScriptStatus status) {
        this.status = status;
    }
    public String getLink() {
        return link;
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public OutputStream getLogStream() {
        return logStream;
    }
    public void setLogStream(OutputStream logStream) {
        this.logStream = logStream;
    }
    public String getOutputInfo() {
        return outputInfo;
    }
    public void setOutputInfo(String outputInfo) {
        this.outputInfo = outputInfo;
    }
}
