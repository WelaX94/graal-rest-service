package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import com.project.graalrestservice.domain.script.exception.*;
import com.project.graalrestservice.domain.script.model.Script;
import com.project.graalrestservice.domain.script.service.service_implementation.ScriptRepositoryImpl;
import com.project.graalrestservice.web.dto.Page;
import com.project.graalrestservice.web.dto.ScriptInfoForList;
import com.project.graalrestservice.web.dto.ScriptInfoForSingle;
import com.project.graalrestservice.web.controller.ScriptsController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.project.graalrestservice.domain.script.enumeration.ScriptStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@SpringBootTest
class ScriptsRootControllerTest {

  @Autowired
  private ScriptsController scriptsController;
  @Autowired
  private ScriptRepositoryImpl scriptRepositoryImpl;
  private Map<String, Script> scriptMap;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException, InstantiationException {
    if (scriptMap == null) {
      Field mapField = ScriptRepositoryImpl.class.getDeclaredField("map");
      mapField.setAccessible(true);
      scriptMap = (Map<String, Script>) mapField.get(scriptRepositoryImpl);
    }
    Constructor<Script> scriptConstructor =
        Script.class.getDeclaredConstructor(String.class, String.class, int.class);
    scriptConstructor.setAccessible(true);
    Field statusField = Script.class.getDeclaredField("status");
    statusField.setAccessible(true);

    int bufCapacity = 65536;

    Script s0 = scriptConstructor.newInstance("q_script", "let a = 0;", bufCapacity);
    statusField.set(s0, IN_QUEUE);

    Script s1 = scriptConstructor.newInstance("r_script", "let a = 0;", bufCapacity);
    statusField.set(s1, RUNNING);

    Script s2 = scriptConstructor.newInstance("c_script", "let a = 0;", bufCapacity);
    statusField.set(s2, EXECUTION_CANCELED);

    Script s3 = scriptConstructor.newInstance("f_script", "let a = 0;", bufCapacity);
    statusField.set(s3, EXECUTION_FAILED);

    Script s4 = scriptConstructor.newInstance("s_script", "let a = 0;", bufCapacity);
    statusField.set(s4, EXECUTION_SUCCESSFUL);

    scriptMap.put("q_script", s0);
    scriptMap.put("r_script", s1);
    scriptMap.put("c_script", s2);
    scriptMap.put("f_script", s3);
    scriptMap.put("s_script", s4);

  }

  @AfterEach
  void tearDown() {
    scriptMap.clear();
  }

  @Test
  void testGetScriptListPageExceptionsThrowing() {
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptListPage(-10, 10, null, null, false, false));
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptListPage(1, -10, null, null, false, false));

    assertThrows(PageDoesNotExistException.class,
        () -> scriptsController.getScriptListPage(6, 1, null, null, false, false));
    assertThrows(PageDoesNotExistException.class,
        () -> scriptsController.getScriptListPage(2, 10, null, null, false, false));
    assertThrows(PageDoesNotExistException.class,
        () -> scriptsController.getScriptListPage(2, 10, IN_QUEUE, null, false, false));
    assertThrows(PageDoesNotExistException.class,
        () -> scriptsController.getScriptListPage(1, 10, null, "somePattern", false, false));

    assertDoesNotThrow(() -> scriptsController.getScriptListPage(2, 4, null, null, false, false));
    assertDoesNotThrow(() -> scriptsController.getScriptListPage(5, 1, null, null, false, false));
    assertDoesNotThrow(
        () -> scriptsController.getScriptListPage(1, 10, EXECUTION_SUCCESSFUL, null, false, false));
    assertDoesNotThrow(() -> scriptsController.getScriptListPage(1, 10, null, "r_s", false, false));
  }

  @Test
  void testGetScriptListPagePagination() {
    Page<List<ScriptInfoForList>> page =
        scriptsController.getScriptListPage(1, 5, null, null, false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    List<ScriptInfoForList> list = page.getList();
    assertEquals(5, list.size());
    assertEquals("s_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("c_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("q_script", list.get(4).getName());

    page = scriptsController.getScriptListPage(2, 3, null, null, false, false).getBody();
    assertEquals(2, page.getPageNumber());
    assertEquals(2, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(2, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(2, list.size());
    assertEquals("r_script", list.get(0).getName());
    assertEquals("q_script", list.get(1).getName());

    page = scriptsController.getScriptListPage(3, 1, null, null, false, false).getBody();
    assertEquals(3, page.getPageNumber());
    assertEquals(5, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("c_script", list.get(0).getName());
  }

  @Test
  void testGetScriptListPageFiltration() { // NOSONAR
    Page<List<ScriptInfoForList>> page =
        scriptsController.getScriptListPage(1, 10, IN_QUEUE, null, false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    List<ScriptInfoForList> list = page.getList();
    assertEquals(1, list.size());
    assertEquals("q_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 10, EXECUTION_SUCCESSFUL, null, false, false)
        .getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("s_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 10, EXECUTION_SUCCESSFUL, "s_scr", false, false)
        .getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("s_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 10, null, "script", false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("s_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("c_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("q_script", list.get(4).getName());

    page = scriptsController.getScriptListPage(1, 10, null, "f", false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("f_script", list.get(0).getName());
  }

  @Test
  void testGetScriptListPageSorting() { // NOSONAR
    Page<List<ScriptInfoForList>> page =
        scriptsController.getScriptListPage(1, 10, null, null, false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    List<ScriptInfoForList> list = page.getList();
    assertEquals(5, list.size());
    assertEquals("s_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("c_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("q_script", list.get(4).getName());

    page = scriptsController.getScriptListPage(1, 10, null, null, false, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("s_script", list.get(4).getName());
    assertEquals("f_script", list.get(3).getName());
    assertEquals("c_script", list.get(2).getName());
    assertEquals("r_script", list.get(1).getName());
    assertEquals("q_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 10, null, null, true, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("c_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("q_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("s_script", list.get(4).getName());

    page = scriptsController.getScriptListPage(1, 10, null, null, true, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("c_script", list.get(4).getName());
    assertEquals("f_script", list.get(3).getName());
    assertEquals("q_script", list.get(2).getName());
    assertEquals("r_script", list.get(1).getName());
    assertEquals("s_script", list.get(0).getName());
  }

  @Test
  void testGetScriptListPageCommon() { // NOSONAR
    Page<List<ScriptInfoForList>> page = scriptsController
        .getScriptListPage(1, 3, EXECUTION_SUCCESSFUL, null, false, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    List<ScriptInfoForList> list = page.getList();
    assertEquals(1, list.size());
    assertEquals("s_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 2, null, "f_scri", false, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("f_script", list.get(0).getName());

    page =
        scriptsController.getScriptListPage(1, 3, EXECUTION_CANCELED, "c_sc", true, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("c_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(2, 1, null, "c", true, false).getBody();
    assertEquals(2, page.getPageNumber());
    assertEquals(5, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("f_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 4, null, "pt", false, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(2, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(4, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(4, list.size());
    assertEquals("s_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("c_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());

    page = scriptsController.getScriptListPage(1, 7, EXECUTION_FAILED, "_", false, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("f_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 6, RUNNING, "r_script", false, true).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(1, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("r_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 10, null, "ri", true, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("c_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("q_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("s_script", list.get(4).getName());

    page = scriptsController.getScriptListPage(3, 2, null, null, true, true).getBody();
    assertEquals(3, page.getPageNumber());
    assertEquals(3, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(1, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(1, list.size());
    assertEquals("c_script", list.get(0).getName());

    page = scriptsController.getScriptListPage(1, 5, null, "scri", true, false).getBody();
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getNumPages());
    assertEquals(5, page.getTotalScripts());
    assertEquals(5, page.getScriptsOnPage());
    list = page.getList();
    assertEquals(5, list.size());
    assertEquals("c_script", list.get(0).getName());
    assertEquals("f_script", list.get(1).getName());
    assertEquals("q_script", list.get(2).getName());
    assertEquals("r_script", list.get(3).getName());
    assertEquals("s_script", list.get(4).getName());
  }

  @Test
  void testRunScriptExceptionsThrowing() {
    assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "!@#"));
    assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "$%^"));
    assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "&*("));
    assertThrows(WrongNameException.class,
        () -> scriptsController.runScript("let a = 0;", "asd(as)"));
    assertThrows(WrongNameException.class,
        () -> scriptsController.runScript("let a = 0;",
            "aaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaa"
                + "aaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaaT"));
    assertThrows(WrongNameException.class,
        () -> scriptsController.runScript("let a = 0;", "q_script"));
    assertThrows(WrongNameException.class,
        () -> scriptsController.runScript("let a = 0;", "f_script"));
    assertThrows(WrongScriptException.class,
        () -> scriptsController.runScript("leta a = 0;", "new1"));

    assertDoesNotThrow(() -> scriptsController.runScript("let a = 0;", "new2"));
    assertDoesNotThrow(() -> scriptsController.runScript("let a = 0;",
        "aaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaa"
            + "aaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaa"));

    assertThrows(WrongNameException.class, () -> scriptsController.runScript("let a = 0;", "new2"));
  }

  @Test
  void testRunAndStopScript() {
    scriptsController.runScript("let a = 0;", "s_scr");
    scriptsController.runScript("console.lottt(1)", "f_scr");
    for (int i = 0; i < 10; i++) {
      scriptsController.runScript("while(true){}", "r_scr_" + i);
    }
    scriptsController.runScript("let a = 0;", "q_scr");

    await().until(fieldIn(scriptMap.get("s_scr")).ofType(ScriptStatus.class).andWithName("status"),
        equalTo(EXECUTION_SUCCESSFUL));
    await().until(fieldIn(scriptMap.get("f_scr")).ofType(ScriptStatus.class).andWithName("status"),
        equalTo(EXECUTION_FAILED));

    assertEquals(EXECUTION_SUCCESSFUL, scriptMap.get("s_scr").getStatus());
    assertEquals(EXECUTION_FAILED, scriptMap.get("f_scr").getStatus());
    assertEquals(IN_QUEUE, scriptMap.get("q_scr").getStatus());

    for (int i = 0; i < 10; i++) {
      final String scrName = "r_scr_" + i;
      assertEquals(RUNNING, scriptMap.get(scrName).getStatus());
      scriptsController.stopScript(scrName);
      await().until(
          fieldIn(scriptMap.get(scrName)).ofType(ScriptStatus.class).andWithName("status"),
          equalTo(EXECUTION_CANCELED));
      assertThrows(WrongScriptStatusException.class, () -> scriptsController.stopScript(scrName));
    }

    await().until(fieldIn(scriptMap.get("q_scr")).ofType(ScriptStatus.class).andWithName("status"),
        equalTo(EXECUTION_SUCCESSFUL));

    assertThrows(WrongScriptStatusException.class, () -> scriptsController.stopScript("s_scr"));
    assertThrows(WrongScriptStatusException.class, () -> scriptsController.stopScript("f_scr"));
    assertThrows(WrongScriptStatusException.class, () -> scriptsController.stopScript("q_scr"));
  }

  @Test
  void testDeleteScriptExceptionsThrowing() {
    assertThrows(ScriptNotFoundException.class, () -> scriptsController.deleteScript("ABC"));

    assertThrows(WrongScriptStatusException.class,
        () -> scriptsController.deleteScript("r_script"));

    assertDoesNotThrow(() -> scriptsController.deleteScript("q_script"));
    assertNull(scriptMap.get("q_script"));
    assertDoesNotThrow(() -> scriptsController.deleteScript("s_script"));
    assertNull(scriptMap.get("s_script"));
    assertDoesNotThrow(() -> scriptsController.deleteScript("f_script"));
    assertNull(scriptMap.get("f_script"));
    assertDoesNotThrow(() -> scriptsController.deleteScript("c_script"));
    assertNull(scriptMap.get("c_script"));
  }

  @Test
  void testRunScriptAndGetScriptLogs() {
    assertThrows(ScriptNotFoundException.class,
        () -> scriptsController.getScriptLogs("test", null, null));

    scriptsController.runScript("console.log('0123456789')", "numbers");

    await().until(
        fieldIn(scriptMap.get("numbers")).ofType(ScriptStatus.class).andWithName("status"),
        equalTo(EXECUTION_SUCCESSFUL));

    assertEquals(EXECUTION_SUCCESSFUL, scriptMap.get("numbers").getStatus());
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptLogs("numbers", -10, null));
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptLogs("numbers", 0, 999999));
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptLogs("numbers", 0, -100));
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptLogs("numbers", 8, 5));
    assertThrows(WrongArgumentException.class,
        () -> scriptsController.getScriptLogs("numbers", 3, 3));

    assertEquals("0123456789\n", scriptsController.getScriptLogs("numbers", 0, null).getBody());
    assertEquals("0123456789\n", scriptsController.getScriptLogs("numbers", 0, 11).getBody());
    assertEquals("\n", scriptsController.getScriptLogs("numbers", 10, 11).getBody());
    assertEquals("0", scriptsController.getScriptLogs("numbers", 0, 1).getBody());
    assertEquals("34567", scriptsController.getScriptLogs("numbers", 3, 8).getBody());
  }

  @Test
  void testRunScriptAndGetScriptCode() {
    assertThrows(ScriptNotFoundException.class, () -> scriptsController.getScriptCode("test"));

    String scriptCode = "for(let a = 0; a < 10; a++){console.log(a)}";
    scriptsController.runScript(scriptCode, "numbers");
    assertEquals(scriptCode, scriptsController.getScriptCode("numbers").getBody());

    scriptCode = "console.loggg(10)";
    scriptsController.runScript(scriptCode, "wrongScript");
    assertEquals(scriptCode, scriptsController.getScriptCode("wrongScript").getBody());
  }

  @Test
  void testRunAndGetSingleScriptInfo() {
    assertThrows(ScriptNotFoundException.class,
        () -> scriptsController.getSingleScriptInfo("notFound"));

    ScriptInfoForSingle script = scriptsController.getSingleScriptInfo("q_script").getBody();
    assertEquals("q_script", script.getName());
    assertEquals(IN_QUEUE, script.getStatus());
    assertNotNull(script.getCreateTime());

    script = scriptsController.getSingleScriptInfo("r_script").getBody();
    assertEquals("r_script", script.getName());
    assertEquals(RUNNING, script.getStatus());
    assertNotNull(script.getCreateTime());

    script = scriptsController.getSingleScriptInfo("c_script").getBody();
    assertEquals("c_script", script.getName());
    assertEquals(EXECUTION_CANCELED, script.getStatus());
    assertNotNull(script.getCreateTime());

    script = scriptsController.getSingleScriptInfo("f_script").getBody();
    assertEquals("f_script", script.getName());
    assertEquals(EXECUTION_FAILED, script.getStatus());
    assertNotNull(script.getCreateTime());

    script = scriptsController.getSingleScriptInfo("s_script").getBody();
    assertEquals("s_script", script.getName());
    assertEquals(EXECUTION_SUCCESSFUL, script.getStatus());
    assertNotNull(script.getCreateTime());

    scriptsController.runScript("console.log('Hello, World!')", "hello");

    await().until(fieldIn(scriptMap.get("hello")).ofType(ScriptStatus.class).andWithName("status"),
        equalTo(EXECUTION_SUCCESSFUL));

    script = scriptsController.getSingleScriptInfo("hello").getBody();
    assertEquals("hello", script.getName());
    assertEquals(EXECUTION_SUCCESSFUL, script.getStatus());
    assertNotNull(script.getCreateTime());
    assertNotNull(script.getStartTime());
    assertNotNull(script.getEndTime());
    assertEquals(14, script.getLogsSize());
  }

  @Test
  void testRunScriptWithLogsStreaming() throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    StreamingResponseBody srb =
        scriptsController.runScriptWithLogsStreaming("console.log('Hello')", "s_scr");
    srb.writeTo(os);
    assertEquals("Hello\n", os.toString());
    assertEquals(EXECUTION_SUCCESSFUL, scriptMap.get("s_scr").getStatus());

    os.reset();
    srb = scriptsController.runScriptWithLogsStreaming("console.logaaa('Hello')", "f_scr");
    srb.writeTo(os);
    assertTrue(os.toString().contains("TypeError: (intermediate value).logaaa is not a function"));
    assertEquals(EXECUTION_FAILED, scriptMap.get("f_scr").getStatus());
  }

}
