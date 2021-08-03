package com.project.graalrestservice.applicationLogic.threads;

import com.project.graalrestservice.applicationLogic.enums.ScriptStatus;
import com.project.graalrestservice.applicationLogic.models.ScriptInfo;
import com.project.graalrestservice.applicationLogic.utils.CircularOutputStream;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScriptExecutionThreadTest {

    String host = "http://localhost";
    int port = 3030;
    Map<String, ScriptInfo> scriptMap;

    @BeforeEach
    void setUp() {
        scriptMap = new HashMap<>();
        ScriptInfo emptyScript = new ScriptInfo("emptyScript","", host, port);
        ScriptInfo emptyLog = new ScriptInfo("emptyLog","var x = 0", host, port);
        String script = "sleep(3000);\n" +
                "\n" +
                "function sleep(millis) {\n" +
                "    var t = (new Date()).getTime();\n" +
                "    var i = 0;\n" +
                "    while (((new Date()).getTime() - t) < millis) {\n" +
                "        i++;\n" +
                "    }\n" +
                "}";
        ScriptInfo longAwaitedScript = new ScriptInfo("longAwaitedScript", script, host, port);
        ScriptInfo mistakeScript = new ScriptInfo("mistakeScript", "vaar x = 0", host, port);
        script = "var x = 5;\n" +
                "var y = 10;\n " +
                "var z = x + y;\n" +
                "console.log(x);\n" +
                "console.log(y);\n" +
                "console.log(z);";
        ScriptInfo correctScript = new ScriptInfo("correctScript", script, host, port);

        scriptMap.put("emptyScript", emptyScript);
        scriptMap.put("longAwaitedScript", longAwaitedScript);
        scriptMap.put("emptyLog", emptyLog);
        scriptMap.put("mistakeScript", mistakeScript);
        scriptMap.put("correctScript", correctScript);

        for(Map.Entry<String, ScriptInfo> element: scriptMap.entrySet()) {
            element.getValue().setLogStream(new CircularOutputStream(65536));
        }
    }

    @Test
    void run() {
        ScriptInfo script = scriptMap.get("emptyScript");
        ScriptExecutionThread thread = new ScriptExecutionThread(script);
        thread.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getError());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("emptyLog");
        thread = new ScriptExecutionThread(script);
        thread.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getError());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("longAwaitedScript");
        thread = new ScriptExecutionThread(script);
        thread.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getError());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("mistakeScript");
        thread = new ScriptExecutionThread(script);
        thread.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        String excepted = "SyntaxError: Unnamed:1:5 Expected ; but found x\n" +
                "vaar x = 0\n" +
                "     ^\n";
        assertEquals(excepted, script.getError());
        assertEquals(ScriptStatus.EXECUTION_FAILED, script.getScriptStatus());

        script = scriptMap.get("correctScript");
        thread = new ScriptExecutionThread(script);
        thread.run();
        assertEquals("Attempting to run a script\n5\n10\n15\n", script.getLogStream().toString());
        assertEquals("", script.getError());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());
    }

}