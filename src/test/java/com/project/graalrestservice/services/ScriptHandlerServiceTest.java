package com.project.graalrestservice.services;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongScriptStatusException;
import com.project.graalrestservice.repositories.ScriptList;
import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ScriptHandlerServiceTest {

    private String host = "http://localhost";
    private int port = 3030;

    private ScriptHandlerService scriptHandler;
    private ConcurrentHashMap<String, ScriptInfo> scriptInfoMap;
    private Context context;
    private static List<String> illegalNamespace;

    @BeforeAll
    static void SetUpIllegalNamespace() {
        illegalNamespace = new ArrayList<>();
        illegalNamespace.add("!@#");
        illegalNamespace.add("$%^");
        illegalNamespace.add("&*(");
        illegalNamespace.add(")+=");
        illegalNamespace.add("ABCabc$");
        illegalNamespace.add("?abcVRTty");
        illegalNamespace.add("AAAAAAAAAAbbbbbbbbbb" +
                "AAAAAAAAAAbbbbbbbbbb" +
                "AAAAAAAAAAbbbbbbbbbb" +
                "AAAAAAAAAAbbbbbbbbbb" +
                "AAAAAAAAAAbbbbbbbbbb" +
                "A");
    }

    @BeforeEach
    void setUp() {
        CircularOutputStream circularOutputStream = new CircularOutputStream(64);
        context = Context.create();
        scriptInfoMap = new ConcurrentHashMap<>();

        ScriptInfo s0_queue = new ScriptInfo("s0_queue", "", host, port);
        s0_queue.setScriptStatus(ScriptStatus.IN_QUEUE);
        s0_queue.setLogStream(circularOutputStream);
        ScriptInfo s1_successful = new ScriptInfo("s1_successful", "", host, port);
        s1_successful.setScriptStatus(ScriptStatus.EXECUTION_SUCCESSFUL);
        s1_successful.setLogStream(circularOutputStream);
        ScriptInfo s2_failed = new ScriptInfo("s2_failed", "", host, port);
        s2_failed.setScriptStatus(ScriptStatus.EXECUTION_FAILED);
        s2_failed.setLogStream(circularOutputStream);
        ScriptInfo s3_stopped = new ScriptInfo("s3_stopped", "", host, port);
        s3_stopped.setScriptStatus(ScriptStatus.EXECUTION_STOPPED);
        s3_stopped.setError("Context execution was cancelled.");
        s3_stopped.setLogStream(circularOutputStream);
        ScriptInfo s4_running = new ScriptInfo("s4_running", "", host, port);
        s4_running.setScriptStatus(ScriptStatus.RUNNING);
        s4_running.setLogStream(circularOutputStream);
        s4_running.setContext(context);

        scriptInfoMap.put("s0_queue", s0_queue);
        scriptInfoMap.put("s1_successful", s1_successful);
        scriptInfoMap.put("s2_failed", s2_failed);
        scriptInfoMap.put("s3_stopped", s3_stopped);
        scriptInfoMap.put("s4_running", s4_running);

        ScriptList scriptList = new ScriptListService(scriptInfoMap);
        scriptHandler = new ScriptHandlerService(scriptList, host, port, Executors.newFixedThreadPool(10));
    }

    @AfterEach
    void tearDown() {
        scriptInfoMap.clear();
        context.close(true);
    }

    @Test
    void addScript() {
        String excepted;
        Throwable thrown;
        for (String scriptName: illegalNamespace) {
            thrown = assertThrows(WrongNameException.class, () -> scriptHandler.addScript(scriptName, "var a = 0"));
            excepted = "Wrong script name. The name uses illegal characters or exceeds the allowed length";
            assertEquals(excepted, thrown.getMessage());
        }
        thrown = assertThrows(WrongNameException.class, () -> scriptHandler.addScript("s2_failed", "var a = 0"));
        excepted = "Wrong script name. Such a name is already in use";
        assertEquals(excepted, thrown.getMessage());

        String scriptName = "newScript";
        excepted = "The script is received and added to the execution queue.\nDetailed information: " + host + ":" + port + "/scripts/" + scriptName;
        assertEquals(excepted, scriptHandler.addScript(scriptName, "var a = 0"));
        thrown = assertThrows(WrongNameException.class, () -> scriptHandler.addScript(scriptName, "var a = 0"));
        excepted = "Wrong script name. Such a name is already in use";
        assertEquals(excepted, thrown.getMessage());
    }

    @Test
    void getScriptInfo() {
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.getScriptInfo("Doesn'tExist"));

        String excepted = "Script: s0_queue\n" +
                "Status: IN_QUEUE\n" +
                "Logs:\n\n";
        assertEquals(excepted, scriptHandler.getScriptInfo("s0_queue"));

        excepted = "Script: s1_successful\n" +
                "Status: EXECUTION_SUCCESSFUL\n" +
                "Logs:\n\n";
        assertEquals(excepted, scriptHandler.getScriptInfo("s1_successful"));

        excepted = "Script: s3_stopped\n" +
                "Status: EXECUTION_STOPPED\n" +
                "Logs:\n\n" +
                "Context execution was cancelled.";
        assertEquals(excepted, scriptHandler.getScriptInfo("s3_stopped"));
    }

    @Test
    void stopScript() {
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.stopScript("Doesn'tExist"));

        Throwable thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.stopScript("s0_queue"));
        String excepted = "Wrong script status - IN_QUEUE. You cannot stop a script that is not running";
        assertEquals(excepted, thrown.getMessage());

        thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.stopScript("s1_successful"));
        excepted = "Wrong script status - EXECUTION_SUCCESSFUL. You cannot stop a script that is not running";
        assertEquals(excepted, thrown.getMessage());

        thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.stopScript("s2_failed"));
        excepted = "Wrong script status - EXECUTION_FAILED. You cannot stop a script that is not running";
        assertEquals(excepted, thrown.getMessage());

        thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.stopScript("s3_stopped"));
        excepted = "Wrong script status - EXECUTION_STOPPED. You cannot stop a script that is not running";
        assertEquals(excepted, thrown.getMessage());

        excepted = "Script 's4_running' stopped";
        assertEquals(excepted, scriptHandler.stopScript("s4_running"));
        scriptInfoMap.get("s4_running").setScriptStatus(ScriptStatus.EXECUTION_STOPPED);
        thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.stopScript("s4_running"));
        excepted = "Wrong script status - EXECUTION_STOPPED. You cannot stop a script that is not running";
        assertEquals(excepted, thrown.getMessage());
    }

    @Test
    void deleteScript() {
        final String s0 = "s0_queue";
        String excepted = "Script '" + s0 + "' deleted";
        assertEquals(excepted, scriptHandler.deleteScript(s0));
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.deleteScript(s0));

        final String s1 = "s1_successful";
        excepted = "Script '" + s1 + "' deleted";
        assertEquals(excepted, scriptHandler.deleteScript(s1));
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.deleteScript(s1));

        final String s2 = "s2_failed";
        excepted = "Script '" + s2 + "' deleted";
        assertEquals(excepted, scriptHandler.deleteScript(s2));
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.deleteScript(s2));

        final String s3 = "s3_stopped";
        excepted = "Script '" + s3 + "' deleted";
        assertEquals(excepted, scriptHandler.deleteScript(s3));
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.deleteScript(s3));

        final String s4= "s4_running";
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.getScriptInfo("Doesn'tExist"));
        Throwable thrown = assertThrows(WrongScriptStatusException.class, () -> scriptHandler.deleteScript(s4));
        excepted = "Wrong script status - RUNNING. To delete a running script, you must first stop it";
        assertEquals(excepted, thrown.getMessage());
        scriptInfoMap.get(s4).setScriptStatus(ScriptStatus.EXECUTION_STOPPED);
        excepted = "Script '" + s4 + "' deleted";
        assertEquals(excepted, scriptHandler.deleteScript(s4));
        assertThrows(ScriptNotFoundException.class, () -> scriptHandler.deleteScript(s4));

    }
}