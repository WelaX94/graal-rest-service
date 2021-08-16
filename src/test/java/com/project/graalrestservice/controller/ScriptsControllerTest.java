package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForSingle;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptRepositoryImpl;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptServiceImpl;
import com.project.graalrestservice.domain.utils.CircularOutputStream;
import com.project.graalrestservice.exceptionHandling.exceptions.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ScriptsControllerTest {

    private ConcurrentHashMap<String, ScriptInfo> map;
    private ScriptsController scriptsController;
    private ScriptService scriptService;
    private ScriptRepository scriptRepository;
    private ExecutorService executorService;
    private int executorServiceNumberOfThreads = 10;
    private int logStreamCapacity = 65536;
    private String scriptsLink = "http://localhost:3030/scripts";
    private HttpServletRequest servletRequest = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        map = new ConcurrentHashMap<>();
        scriptRepository = new ScriptRepositoryImpl(map);
        executorService = Executors.newFixedThreadPool(executorServiceNumberOfThreads);
        scriptService = new ScriptServiceImpl(scriptRepository, executorService, logStreamCapacity);
        scriptsController = new ScriptsController(scriptService);

        ScriptInfo s0 = new ScriptInfo(
                "q_script",
                "let a = 0;",
                scriptsLink + "/q_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                executorService,
                ScriptStatus.IN_QUEUE);
        ScriptInfo s1 = new ScriptInfo(
                "r_script",
                "let a = 0;",
                scriptsLink + "/r_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                executorService,
                ScriptStatus.RUNNING);
        ScriptInfo s2 = new ScriptInfo(
                "c_script",
                "let a = 0;",
                scriptsLink + "/c_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                executorService,
                ScriptStatus.EXECUTION_CANCELED);
        ScriptInfo s3 = new ScriptInfo(
                "f_script",
                "let a = 0;",
                scriptsLink + "/f_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                executorService,
                ScriptStatus.EXECUTION_FAILED);
        ScriptInfo s4 = new ScriptInfo(
                "s_script",
                "let a = 0;",
                scriptsLink + "/s_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                executorService,
                ScriptStatus.EXECUTION_SUCCESSFUL);

        map.put(s0.getName(), s0);
        map.put(s1.getName(), s1);
        map.put(s2.getName(), s2);
        map.put(s3.getName(), s3);
        map.put(s4.getName(), s4);

    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
    }

    @Test
    void getScriptListPage() {
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("basic", 10, 10));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("basic", -1, 10));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("basic", 10, -10));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("qwerty", 10, 10));

        ScriptListPage scriptListPage = scriptService.getScriptListPage("basic", 10, 1);
        assertEquals(scriptListPage.getScriptList().size(), 5);
        assertEquals(scriptListPage.getScriptsOnPage(), 5);
        assertEquals(scriptListPage.getTotalScripts(), 5);
        assertEquals(scriptListPage.getPage(), "1 of 1");
        assertEquals(scriptListPage.getScriptList().get(0).getStatus(), ScriptStatus.EXECUTION_SUCCESSFUL);
        assertEquals(scriptListPage.getScriptList().get(1).getStatus(), ScriptStatus.EXECUTION_FAILED);
        assertEquals(scriptListPage.getScriptList().get(2).getStatus(), ScriptStatus.EXECUTION_CANCELED);
        assertEquals(scriptListPage.getScriptList().get(3).getStatus(), ScriptStatus.RUNNING);
        assertEquals(scriptListPage.getScriptList().get(4).getStatus(), ScriptStatus.IN_QUEUE);

        scriptListPage = scriptService.getScriptListPage("basic", 2, 2);
        assertEquals(scriptListPage.getScriptList().size(), 2);
        assertEquals(scriptListPage.getScriptsOnPage(), 2);
        assertEquals(scriptListPage.getTotalScripts(), 5);
        assertEquals(scriptListPage.getPage(), "2 of 3");

        scriptListPage = scriptService.getScriptListPage("basic", 2, 3);
        assertEquals(scriptListPage.getScriptList().size(), 1);
        assertEquals(scriptListPage.getScriptsOnPage(), 1);
        assertEquals(scriptListPage.getTotalScripts(), 5);
        assertEquals(scriptListPage.getPage(), "3 of 3");

        scriptListPage = scriptService.getScriptListPage("fr", 1, 2);
        assertEquals(scriptListPage.getScriptList().size(), 1);
        assertEquals(scriptListPage.getScriptsOnPage(), 1);
        assertEquals(scriptListPage.getTotalScripts(), 2);
        assertEquals(scriptListPage.getPage(), "2 of 2");
        assertEquals(scriptListPage.getScriptList().get(0).getStatus(), ScriptStatus.RUNNING);

        scriptListPage = scriptService.getScriptListPage("rqsc", 2, 2);
        assertEquals(scriptListPage.getScriptList().size(), 2);
        assertEquals(scriptListPage.getScriptsOnPage(), 2);
        assertEquals(scriptListPage.getTotalScripts(), 4);
        assertEquals(scriptListPage.getPage(), "2 of 2");
        assertEquals(scriptListPage.getScriptList().get(0).getStatus(), ScriptStatus.EXECUTION_SUCCESSFUL);
        assertEquals(scriptListPage.getScriptList().get(1).getStatus(), ScriptStatus.EXECUTION_CANCELED);

    }

    @Test
    void runScript() throws InterruptedException {
        assertThrows(
                WrongArgumentException.class,
                () -> scriptsController.runScript("", "abc", "s", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "q_script", "f", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "s_script", "f", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "qw@rty", "f", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "!@#", "f", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "$%^", "f", servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "&*(", "f", servletRequest));
        String longName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee" +
                "ffffffffffgggggggggghhhhhhhhhhiiiiiiiiiijjjjjjjjjjk";
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", longName, "f", servletRequest));
        assertThrows(
                WrongScriptException.class,
                () -> scriptsController.runScript("leta a = 0;", "abc", "f", servletRequest));

        ResponseEntity<ScriptInfoForSingle> response = scriptsController.runScript("let a = 0;", "s0", "b", servletRequest);
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, response.getBody().getStatus());
        assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "s0", "b", servletRequest));
        response = scriptsController.runScript("console.qwerty()", "s1", "b", servletRequest);
        assertEquals(ScriptStatus.EXECUTION_FAILED, response.getBody().getStatus());
        response = scriptsController.runScript("while(true){}", "sr0", "f", servletRequest);
        Thread.sleep(1000);
        ScriptInfo scriptInfo = map.get("sr0");
        assertEquals(ScriptStatus.RUNNING, scriptInfo.getScriptStatus());
        scriptInfo.stopScriptExecution();
        Thread.sleep(1000);
        assertEquals(ScriptStatus.EXECUTION_CANCELED, scriptInfo.getScriptStatus());

    }

    @Test
    void getSingleScriptInfo() {
        assertThrows(ScriptNotFoundException.class, () -> scriptsController.getSingleScriptInfo("abc"));

        ScriptInfoForSingle scriptInfoForSingle = scriptsController.getSingleScriptInfo("q_script");
        assertEquals(ScriptStatus.IN_QUEUE, scriptInfoForSingle.getStatus());
        assertEquals("q_script", scriptInfoForSingle.getName());

        scriptInfoForSingle = scriptsController.getSingleScriptInfo("r_script");
        assertEquals(ScriptStatus.RUNNING, scriptInfoForSingle.getStatus());
        assertEquals("r_script", scriptInfoForSingle.getName());

        scriptInfoForSingle = scriptsController.getSingleScriptInfo("s_script");
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, scriptInfoForSingle.getStatus());
        assertEquals("s_script", scriptInfoForSingle.getName());
    }

    @Test
    void getScriptLogs() {
        assertThrows(ScriptNotFoundException.class, () -> scriptsController.getSingleScriptInfo("abc"));
    }

    @Test
    void runScriptWithLogsStreaming() {
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScriptWithLogsStreaming("", "c_script", servletRequest));
        assertDoesNotThrow(() -> scriptsController.runScriptWithLogsStreaming("", "test", servletRequest));
    }

    @Test
    void stopScript() throws InterruptedException {
        assertThrows(
                WrongScriptStatusException.class,
                () -> scriptsController.stopScript("q_script"));
        assertThrows(
                WrongScriptStatusException.class,
                () -> scriptsController.stopScript("c_script"));
        assertThrows(
                WrongScriptStatusException.class,
                () -> scriptsController.stopScript("f_script"));
        assertThrows(
                WrongScriptStatusException.class,
                () -> scriptsController.stopScript("s_script"));
        assertDoesNotThrow(
                () -> scriptsController.stopScript("r_script"));

        Context context = Context.newBuilder().build();
        Value value = context.parse("js", "while(true){}");
        ScriptInfo scriptInfo = new ScriptInfo(
                "stopTest",
                "while(true){}",
                "",
                new CircularOutputStream(logStreamCapacity, false),
                value,
                context,
                executorService);
        map.put(scriptInfo.getName(), scriptInfo);
        executorService.execute(scriptInfo);
        Thread.sleep(1000);
        assertDoesNotThrow(
                () -> scriptsController.stopScript("stopTest"));
        assertEquals(ScriptStatus.EXECUTION_CANCELED, scriptInfo.getScriptStatus());

    }

    @Test
    void deleteScript() {
        assertThrows(
                WrongScriptStatusException.class,
                () -> scriptsController.deleteScript("r_script"));
        assertDoesNotThrow(
                () -> scriptsController.deleteScript("q_script"));
        assertDoesNotThrow(
                () -> scriptsController.deleteScript("s_script"));
        assertDoesNotThrow(
                () -> scriptsController.deleteScript("f_script"));
        assertDoesNotThrow(
                () -> scriptsController.deleteScript("c_script"));

        assertThrows(
                ScriptNotFoundException.class,
                () -> scriptsController.deleteScript("q_script"));
        assertThrows(
                ScriptNotFoundException.class,
                () -> scriptsController.deleteScript("s_script"));
        assertThrows(
                ScriptNotFoundException.class,
                () -> scriptsController.deleteScript("f_script"));
        assertThrows(
                ScriptNotFoundException.class,
                () -> scriptsController.deleteScript("c_script"));
    }
}