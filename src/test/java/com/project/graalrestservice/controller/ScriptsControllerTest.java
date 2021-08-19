package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.exceptions.*;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptRepository;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ScriptsControllerTest {

    private ConcurrentHashMap<String, Script> map;
    private ScriptsController scriptsController;
    private ScriptService scriptService;
    private ScriptRepository scriptRepository;
    private int logStreamCapacity = 65536;
    private String scriptsLink = "http://localhost:3030/scripts";
    private HttpServletRequest servletRequest = new MockHttpServletRequest();
/*      Need to change scripts setup
    @BeforeEach
    void setUp() {
        map = new ConcurrentHashMap<>();
        scriptRepository = new ScriptRepositoryImpl(map);
        scriptService = new ScriptServiceImpl(scriptRepository, logStreamCapacity);
        scriptsController = new ScriptsController(scriptService);

        Script s0 = new Script(
                "q_script",
                "let a = 0;",
                scriptsLink + "/q_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                ScriptStatus.IN_QUEUE);
        Script s1 = new Script(
                "r_script",
                "let a = 0;",
                scriptsLink + "/r_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                ScriptStatus.RUNNING);
        Script s2 = new Script(
                "c_script",
                "let a = 0;",
                scriptsLink + "/c_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                ScriptStatus.EXECUTION_CANCELED);
        Script s3 = new Script(
                "f_script",
                "let a = 0;",
                scriptsLink + "/f_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                ScriptStatus.EXECUTION_FAILED);
        Script s4 = new Script(
                "s_script",
                "let a = 0;",
                scriptsLink + "/s_script",
                new CircularOutputStream(logStreamCapacity,false),
                null,
                Context.newBuilder().build(),
                ScriptStatus.EXECUTION_SUCCESSFUL);

        map.put(s0.getName(), s0);
        map.put(s1.getName(), s1);
        map.put(s2.getName(), s2);
        map.put(s3.getName(), s3);
        map.put(s4.getName(), s4);

    }
*/
    @AfterEach
    void tearDown() {
    }

    @Test
    void getScriptListPage() {
        assertThrows(PageDoesNotExistException.class, () -> scriptService.getScriptListPage("basic", 10, 10));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("basic", -1, 1));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("basic", 10, -10));
        assertThrows(WrongArgumentException.class, () -> scriptService.getScriptListPage("qwerty", 10, 1));

        Page<List<ScriptInfoForList>> scriptListPage = scriptService.getScriptListPage("basic", 10, 1);
        assertEquals(5, scriptListPage.getList().size());
        assertEquals(5, scriptListPage.getScriptsOnPage());
        assertEquals(5, scriptListPage.getTotalScripts());
        assertEquals(1, scriptListPage.getPage());
        assertEquals(1, scriptListPage.getNumPages());
        assertEquals(ScriptStatus.EXECUTION_CANCELED, scriptListPage.getList().get(0).getStatus());
        assertEquals(ScriptStatus.EXECUTION_FAILED, scriptListPage.getList().get(1).getStatus());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, scriptListPage.getList().get(2).getStatus());
        assertEquals(ScriptStatus.RUNNING, scriptListPage.getList().get(3).getStatus());
        assertEquals(ScriptStatus.IN_QUEUE, scriptListPage.getList().get(4).getStatus());

        scriptListPage = scriptService.getScriptListPage("basic", 2, 2);
        assertEquals(2, scriptListPage.getList().size());
        assertEquals(2, scriptListPage.getScriptsOnPage());
        assertEquals(5, scriptListPage.getTotalScripts());
        assertEquals(2, scriptListPage.getPage());
        assertEquals(3, scriptListPage.getNumPages());

        scriptListPage = scriptService.getScriptListPage("basic", 2, 3);
        assertEquals(1, scriptListPage.getList().size());
        assertEquals(1, scriptListPage.getScriptsOnPage());
        assertEquals(5, scriptListPage.getTotalScripts());
        assertEquals(3, scriptListPage.getPage());
        assertEquals(3, scriptListPage.getNumPages());

        scriptListPage = scriptService.getScriptListPage("fr", 1, 2);
        assertEquals(1, scriptListPage.getList().size());
        assertEquals(1, scriptListPage.getScriptsOnPage());
        assertEquals(2, scriptListPage.getTotalScripts());
        assertEquals(2, scriptListPage.getPage());
        assertEquals(2, scriptListPage.getNumPages());
        assertEquals(ScriptStatus.RUNNING, scriptListPage.getList().get(0).getStatus());

        scriptListPage = scriptService.getScriptListPage("rqsc", 2, 2);
        assertEquals(2, scriptListPage.getList().size());
        assertEquals(2, scriptListPage.getScriptsOnPage());
        assertEquals(4, scriptListPage.getTotalScripts());
        assertEquals(2, scriptListPage.getPage());
        assertEquals(2, scriptListPage.getNumPages());
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, scriptListPage.getList().get(0).getStatus());
        assertEquals(ScriptStatus.EXECUTION_CANCELED, scriptListPage.getList().get(1).getStatus());

    }

    @Test
    void runScript() throws InterruptedException {
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "q_script", true, servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "s_script", true, servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "qw@rty", true, servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "!@#", true, servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "$%^", true, servletRequest));
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", "&*(", true, servletRequest));
        String longName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee" +
                "ffffffffffgggggggggghhhhhhhhhhiiiiiiiiiijjjjjjjjjjk";
        assertThrows(
                WrongNameException.class,
                () -> scriptsController.runScript("", longName, true, servletRequest));
        assertThrows(
                WrongScriptException.class,
                () -> scriptsController.runScript("leta a = 0;", "abc", true, servletRequest));

        ResponseEntity<ScriptInfoForSingle> response = scriptsController.runScript("let a = 0;", "s0", false, servletRequest);
        assertEquals(ScriptStatus.EXECUTION_SUCCESSFUL, response.getBody().getStatus());
        assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "s0", false, servletRequest));
        response = scriptsController.runScript("console.qwerty()", "s1", false, servletRequest);
        assertEquals(ScriptStatus.EXECUTION_FAILED, response.getBody().getStatus());

/*          need to configure the script to run
        response = scriptsController.runScript("while(true){}", "sr0", true, servletRequest);
        Thread.sleep(2000);
        Script script = map.get("sr0");
        assertEquals(ScriptStatus.RUNNING, script.getScriptStatus());
        script.stopScriptExecution();
        Thread.sleep(2000);
        assertEquals(ScriptStatus.EXECUTION_CANCELED, script.getScriptStatus());
*/
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

/*          Need to configure the script to run
        Context context = Context.newBuilder().build();
        Value value = context.parse("js", "while(true){}");
        Script script = new Script(
                "stopTest",
                "while(true){}",
                "",
                new CircularOutputStream(logStreamCapacity, false),
                value,
                context);
        map.put(script.getName(), script);

        Thread.sleep(2000);
        assertDoesNotThrow(
                () -> scriptsController.stopScript("stopTest"));
        assertEquals(ScriptStatus.EXECUTION_CANCELED, script.getScriptStatus());
*/
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