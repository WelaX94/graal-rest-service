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

    String link = "http://localhost:3030/";
    Map<String, ScriptInfo> scriptMap;

    @BeforeEach
    void setUp() {
        scriptMap = new HashMap<>();
        ScriptInfo emptyScript = new ScriptInfo("", link + "emptyScript");
        ScriptInfo emptyLog = new ScriptInfo("var x = 0", link + "emptyLog");
        String script = "sleep(3000);\n" +
                "\n" +
                "function sleep(millis) {\n" +
                "    var t = (new Date()).getTime();\n" +
                "    var i = 0;\n" +
                "    while (((new Date()).getTime() - t) < millis) {\n" +
                "        i++;\n" +
                "    }\n" +
                "}";
        ScriptInfo longAwaitedScript = new ScriptInfo(script, link + "longAwaitedScript");
        ScriptInfo mistakeScript = new ScriptInfo( "vaar x = 0", link + "mistakeScript");
        script = "var x = 5;\n" +
                "var y = 10;\n " +
                "var z = x + y;\n" +
                "console.log(x);\n" +
                "console.log(y);\n" +
                "console.log(z);";
        ScriptInfo correctScript = new ScriptInfo(script, link + "correctScript");

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