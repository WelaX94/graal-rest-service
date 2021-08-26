package com.project.graalrestservice.controller;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.exceptions.PageDoesNotExistException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongArgumentException;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptRepository;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptService;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import com.project.graalrestservice.representationModels.mappers.ListScriptMapper;
import com.project.graalrestservice.representationModels.mappers.SingleScriptMapper;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/** Controller class responsible for "/scripts" */
@RestController
@RequestMapping("/scripts")
public class ScriptsController {

  private static final Logger logger = LoggerFactory.getLogger(ScriptsController.class);
  private static final String SCRIPT_REQUEST_PROCESSED = "[{}] - Request successfully processed";
  private static final String REQUEST_PARAMETERS =
      "Parameters: [page={}, pageSize={}, status={}, nameContains={}, orderByName={}, reverseOrder={}]";
  private static final String MDC_NAME_IDENTIFIER = "scriptName";
  private final ScriptService scriptService;

  /**
   * Basic constructor
   */
  @Autowired
  public ScriptsController(ScriptService scriptService) {
    this.scriptService = scriptService;
  }

  /**
   * The method returns a sorted, filtered and paginated JSON {@link Page} with
   * {@link ScriptInfoForList}. The primary List with {@link Script} is
   * {@link ScriptRepository#getScriptList(ScriptStatus, String) filtered} at the
   * {@link ScriptRepository} level. It is then
   * {@link ScriptService#getScriptList(ScriptStatus, String, boolean, boolean) sorted} by
   * {@link ScriptService}.
   * {@link ScriptsController#convertListToPage(List, int, int, String, String, boolean, boolean)
   * Pagination and conversion} to {@link Page} with {@link ScriptInfoForList} occurs at the
   * controller (this) level. By default, no filters are applied and sorting is done by date of
   * script creation.
   * 
   * @param pageNumber the number of the page you are requesting. If not specified, the default
   *        value is '1'.
   * @param pageSize maximum number of scripts per page. If not specified, the default value is
   *        '10'.
   * @param status this parameter is a filter. Allows you to specify the scripts with which statuses
   *        you are interested in the output. If not specified, scripts with all statuses will be
   *        displayed.
   * @param nameContains this parameter is a filter. It allows you to specify an expression that
   *        must contain the name of the script. If not specified, scripts with all names will be
   *        displayed
   * @param orderByName sorting parameter. If true, it will be sorted by script name. By default,
   *        (false), sorting is set to the date the script was created.
   * @param reverseOrder parameter to reverse the sorting. The default is false.
   * @return a sorted, filtered and paginated {@link Page} with List of {@link ScriptInfoForList}.
   */
  @GetMapping
  public ResponseEntity<Page<List<ScriptInfoForList>>> getScriptListPage(
      @RequestParam(defaultValue = "1") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String nameContains,
      @RequestParam(defaultValue = "false") boolean orderByName,
      @RequestParam(defaultValue = "false") boolean reverseOrder) {
    logger.info("Script list request received. " + REQUEST_PARAMETERS, pageNumber, pageSize, status,
        nameContains, orderByName, reverseOrder);
    MDC.put(MDC_NAME_IDENTIFIER, "GetScriptsMethod");
    List<Script> scriptList = scriptService.getScriptList(ScriptStatus.getStatus(status),
        nameContains, orderByName, reverseOrder);
    Page<List<ScriptInfoForList>> scriptPage = convertListToPage(scriptList, pageNumber, pageSize,
        status, nameContains, orderByName, reverseOrder);
    logger.info("Script list request successfully processed. " + REQUEST_PARAMETERS, pageNumber,
        pageSize, status, nameContains, orderByName, reverseOrder);
    return new ResponseEntity<>(scriptPage, HttpStatus.OK);
  }

  /**
   * Method for adding a new script to the run queue. Firstly, the new {@link Script} is added to
   * {@link ScriptRepository#putScript(String, Script) ScriptRepository} via
   * {@link ScriptService#addScript(String, String) ScriptRepository} with the passage of some
   * additional checks. If successful, the script is
   * {@link ScriptService#startScriptAsynchronously(Script) added to the execution queue}
   * (asynchronously) and the user is returned JSON with information about the script at the time it
   * was created.
   * 
   * @param scriptCode JS body
   * @param scriptName a unique identifier of the script, by which it can be accessed in the future
   * @return ScriptInfoForSingle with information about the script at the time it was created
   */
  @PutMapping(value = "/{scriptName}")
  public ResponseEntity<ScriptInfoForSingle> runScript(@RequestBody String scriptCode,
      @PathVariable String scriptName) {
    logger.info("[{}] - A new script is requested to run", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    Script script = scriptService.addScript(scriptName, scriptCode);
    scriptService.startScriptAsynchronously(script);
    ScriptInfoForSingle scriptInfoForSingle = SingleScriptMapper.forSingle.map(script);
    scriptInfoForSingle.setLinks();
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
    return new ResponseEntity<>(scriptInfoForSingle, HttpStatus.CREATED);
  }

  /**
   * A method for obtaining information about the script
   * 
   * @param scriptName script name (identifier)
   * @return ScriptInfoForSingle JSON information about script
   */
  @GetMapping(value = "/{scriptName}")
  public ResponseEntity<ScriptInfoForSingle> getSingleScriptInfo(@PathVariable String scriptName) {
    logger.info("[{}] - Single script info request received", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    ScriptInfoForSingle scriptInfoForSingle =
        SingleScriptMapper.forSingle.map(scriptService.getScript(scriptName));
    scriptInfoForSingle.setLinks();
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
    return new ResponseEntity<>(scriptInfoForSingle, HttpStatus.OK);
  }

  /**
   * A method for getting the output logs of a script. By default, it returns full logs, but you can
   * limit the boundaries from-to if necessary (either together or separately). Important: The
   * numbering of the logs starts from 0.
   * 
   * @param scriptName script name (identifier)
   * @param from the beginning index, inclusive.
   * @param to the ending index, exclusive.
   * @return full logs or logs from from-to range
   * @throws WrongArgumentException if range is entered incorrectly (from/to is less than 0 or
   *         greater than the log length; from >= to)
   */
  @GetMapping(value = "/{scriptName}/logs")
  public ResponseEntity<String> getScriptLogs(@PathVariable String scriptName,
      @RequestParam(required = false, defaultValue = "0") Integer from,
      @RequestParam(required = false) Integer to) {
    logger.info("[{}] - Script logs request received (from={}, to={})", scriptName, from, to);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    String logs = scriptService.getScript(scriptName).getOutputLogs();
    if (to == null)
      to = logs.length();
    try {
      if (from.equals(to))
        throw new StringIndexOutOfBoundsException();
      logs = logs.substring(from, to);
      logger.trace("[{}] - Try block passed without errors", scriptName);
    } catch (StringIndexOutOfBoundsException e) {
      logger.debug("[{}] - Caught StringIndexOutOfBoundsException. Indexes entered incorrectly",
          scriptName);
      throw new WrongArgumentException("The 'from-to' range is entered incorrectly");
    }
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
    return new ResponseEntity<>(logs, HttpStatus.OK);
  }

  /**
   * A method for viewing a script that has been sent to run
   * 
   * @param scriptName script name (identifier)
   * @return String with JS body
   */
  @GetMapping(value = "/{scriptName}/script")
  public ResponseEntity<String> getScriptCode(@PathVariable String scriptName) {
    logger.info("[{}] - Script code request received", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    String scriptCode = scriptService.getScript(scriptName).getScriptCode();
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
    return new ResponseEntity<>(scriptCode, HttpStatus.OK);
  }

  /**
   * Adds a new script to the execution queue with that ability to stream logs in real time. As with
   * {@link ScriptsController#runScript(String, String) first variant}, the script is first added to
   * the repository with some checks, then the StreamingResponseBody is returns. In
   * StreamingResponseBody the script is added to the execution queue (asynchronously) and log
   * streaming begins.
   * 
   * @param scriptCode JS body
   * @param scriptName script name (identifier)
   * @return StreamingResponseBody which broadcasts logs in real time
   */
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PutMapping(value = "/{scriptName}/logs")
  public StreamingResponseBody runScriptWithLogsStreaming(@RequestBody String scriptCode,
      @PathVariable String scriptName) {
    logger.info("[{}] - Script run with logs streaming request received", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    Script script = scriptService.addScript(scriptName, scriptCode);
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
    return (OutputStream outputStream) -> {
      logger.info("[{}] - Streaming logs started", scriptName);
      script.addStreamForRecording(outputStream);
      try {
        outputStream.flush();
        scriptService.startScriptAsynchronously(script);
        while (script.getStatus() == ScriptStatus.IN_QUEUE
            || script.getStatus() == ScriptStatus.RUNNING) {
          Thread.sleep(100);
        }
      } catch (ClientAbortException | InterruptedException e) { // NOSONAR
        logger.info("[{}] - Client terminated the connection ({}).", scriptName, e.getMessage());
      } finally {
        script.deleteStreamForRecording(outputStream);
      }
      logger.info("[{}] - Streaming logs finished", scriptName);
    };
  }

  /**
   * This method is needed to stop a running script. The {@link ScriptService#stopScript(String)
   * ScriptService method} is used to stop the script, which performs additional checks (you can
   * only stop the script with the {@link ScriptStatus status} RUNNING) and if everything is
   * successful, the script stops executing.
   * 
   * @param scriptName script name (identifier)
   */
  @PostMapping(value = "/{scriptName}")
  public void stopScript(@PathVariable String scriptName) {
    logger.info("[{}] - Stop script request received", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    scriptService.stopScript(scriptName);
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
  }

  /**
   * This method is needed to delete a {@link Script script} from {@link ScriptRepository}. The
   * {@link ScriptService#deleteScript(String) ScriptService method} is used to delete the
   * {@link Script script}, which performs additional checks (you can't delete the script with the
   * {@link ScriptStatus status} RUNNING) and if everything is successful, the script deleting from
   * repository.
   * 
   * @param scriptName script name (identifier)
   */
  @DeleteMapping(value = "/{scriptName}")
  public void deleteScript(@PathVariable String scriptName) {
    logger.info("[{}] - Delete script request received", scriptName);
    MDC.put(MDC_NAME_IDENTIFIER, scriptName);
    scriptService.deleteScript(scriptName);
    logger.info(SCRIPT_REQUEST_PROCESSED, scriptName);
  }

  /**
   * A sub-method, which is necessary for
   * {@link ScriptsController#getScriptListPage(int, int, String, String, boolean, boolean)
   * getScriptListPage}. It paginates and converts List to {@link Page}. The following parameters
   * (except scriptList) are needed to create links and fill in information about the page.
   * 
   * @param scriptList script list to be converted
   * @param pageNumber the number of the page you are requesting.
   * @param pageSize maximum number of scripts per page.
   * @param status status filter.
   * @param nameContains name pattern filter.
   * @param orderByName sorting parameter.
   * @param reverseOrder parameter to reverse the sorting.
   * @return paginated {@link Page} with List of {@link ScriptInfoForList}.
   */
  private Page<List<ScriptInfoForList>> convertListToPage(List<Script> scriptList, int pageNumber,
      int pageSize, String status, String nameContains, boolean orderByName, boolean reverseOrder) {
    logger.trace(
        "Starts converting List<Script> to Page<List<ScriptInfoForList>>. " + REQUEST_PARAMETERS,
        pageNumber, pageSize, status, nameContains, orderByName, reverseOrder);

    if (pageNumber < 1)
      throw new WrongArgumentException("The page number cannot be less than 1");
    if (pageSize < 1)
      throw new WrongArgumentException("The page size cannot be less than 1");

    int listSize = scriptList.size();
    int end = pageNumber * pageSize;
    int start = end - pageSize;
    if (start >= listSize)
      throw new PageDoesNotExistException(pageNumber);
    if (listSize < end)
      end = listSize;

    List<ScriptInfoForList> pageList = new ArrayList<>();
    for (; start < end; start++) {
      ScriptInfoForList sifl = ListScriptMapper.forList.map(scriptList.get(start));
      sifl.setLinks();
      pageList.add(sifl);
    }

    int numPages = (listSize % pageSize == 0) ? (listSize / pageSize) : (listSize / pageSize + 1);
    Page<List<ScriptInfoForList>> scriptsPage =
        new Page<>(pageList, pageNumber, numPages, listSize);
    scriptsPage.setLinks(pageSize, status, nameContains, orderByName, reverseOrder);

    logger.trace(
        "Converting List<Script> to Page<List<ScriptInfoForList>> completed successfully. "
            + REQUEST_PARAMETERS,
        pageNumber, pageSize, status, nameContains, orderByName, reverseOrder);
    return scriptsPage;
  }

}
