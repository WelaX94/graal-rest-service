package com.project.graalrestservice.domain.models;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScriptInfoTest {

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

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        ScriptInfo script = scriptMap.get("emptyScript");
        script.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getOutputInfo());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("emptyLog");
        script.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getOutputInfo());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("longAwaitedScript");
        script.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        assertEquals("", script.getOutputInfo());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());

        script = scriptMap.get("mistakeScript");
        script.run();
        assertEquals("Attempting to run a script\n", script.getLogStream().toString());
        String excepted = "SyntaxError: Unnamed:1:5 Expected ; but found x\n" +
                "vaar x = 0\n" +
                "     ^\n";
        assertEquals(excepted, script.getOutputInfo());
        assertEquals(ScriptStatus.EXECUTION_FAILED, script.getScriptStatus());

        script = scriptMap.get("correctScript");
        script.run();
        assertEquals("Attempting to run a script\n5\n10\n15\n", script.getLogStream().toString());
        assertEquals("", script.getOutputInfo());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, script.getScriptStatus());
    }
}