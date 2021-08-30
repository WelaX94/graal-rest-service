package com.project.graalrestservice.domain.script.service.service_implementation;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import com.project.graalrestservice.domain.script.model.Script;
import com.project.graalrestservice.domain.script.service.ScriptRepository;
import com.project.graalrestservice.domain.script.service.ScriptService;
import com.project.graalrestservice.domain.script.exception.WrongNameException;
import com.project.graalrestservice.domain.script.exception.WrongScriptException;
import com.project.graalrestservice.domain.script.exception.WrongScriptStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.project.graalrestservice.domain.script.enumeration.ScriptStatus.*;

/**
 * A class for processing commands over scripts
 */
@Service
public class ScriptServiceImpl implements ScriptService {

  private static final Logger logger = LoggerFactory.getLogger(ScriptService.class); // NOSONAR
  private final ScriptRepository scriptRepository;
  private final int streamCapacity;
  private final Pattern correctlyScriptName = Pattern.compile("^[A-Za-z0-9-_]{0,100}$");

  /**
   * Basic constructor
   */
  @Autowired
  public ScriptServiceImpl(ScriptRepository scriptRepository,
      @org.springframework.beans.factory.annotation.Value("${scripts.outputStream.capacity}") int streamCapacity) {
    this.scriptRepository = scriptRepository;
    this.streamCapacity = streamCapacity;
  }

  /**
   * The method returns a sorted and filtered list of scripts by specified parameters. The list is
   * generated and filtered at the {@link ScriptRepository#getScriptList(ScriptStatus, String)
   * repository level} and sorted at the service (this) level. By default, no filters are applied
   * and sorting is done by date of script creation.
   * 
   * @param scriptStatus this parameter is a filter. Allows you to specify the scripts with which
   *        statuses you are interested in the output. If null - scripts with all statuses will be
   *        displayed.
   * @param nameContains this parameter is a filter. It allows you to specify an expression that
   *        must contain the name of the script. If null - scripts with all names will be displayed
   * @param orderByName sorting parameter. If true, it will be sorted by script name.
   * @param reverseOrder parameter to reverse the sorting.
   * @return sorted and filtered {@link List} of {@link Script}.
   */
  public List<Script> getScriptList(ScriptStatus scriptStatus, String nameContains,
      boolean orderByName, boolean reverseOrder) {
    Comparator<Script> comparator;
    if (orderByName)
      comparator = Comparator.comparing(Script::getName);
    else
      comparator = Comparator.comparing(Script::getCreateTime).reversed();
    if (reverseOrder)
      comparator = comparator.reversed();
    List<Script> scriptList = scriptRepository.getScriptList(scriptStatus, nameContains).stream()
        .sorted(comparator).collect(Collectors.toList());
    logger.debug(
        "Script service return filtered and sorted script list. Parameters [scriptStatus={}, nameContains={}, orderByName={}, reverseOrder={}]",
        scriptStatus, nameContains, orderByName, reverseOrder);
    return scriptList;
  }

  /**
   * A method for adding a new script to the system. Firstly, {@link #checkName(String) the name is
   * checked} to see if it matches the {@link #correctlyScriptName specified pattern}. After that,
   * its validity is checked when {@link Script#create(String, String, int) creating a Script
   * object}. And if everything is good, then it will be
   * {@link ScriptRepository#putScript(String, Script) added to the system} (if there is no script
   * with the same name).
   * 
   * @param scriptName script name (identifier)
   * @param scriptCode JS body
   * @return created Script object
   */
  @Override
  public Script addScript(String scriptName, String scriptCode) {
    checkName(scriptName);
    Script script = Script.create(scriptName, scriptCode, this.streamCapacity);
    scriptRepository.putScript(scriptName, script);
    logger.debug("[{}] - New script was added to the service", scriptName);
    return script;
  }

  /**
   * Method for running the script in asynchronous mode
   * 
   * @param script launch script
   */
  @Override
  public void startScriptAsynchronously(Script script) {
    logger.debug("[{}] - Starting script in asynchronously mode", script.getName());
    script.run();
  }

  /**
   * A method to get information about the script you are looking for
   * 
   * @param scriptName script name (identifier)
   * @return Script information about the script
   */
  @Override
  public Script getScript(String scriptName) {
    Script script = scriptRepository.getScript(scriptName);
    logger.debug("[{}] - Returning the script", scriptName);
    return script;
  }

  /**
   * Method for stopping a running script. You can only stop a script with the status RUNNING.
   * 
   * @param scriptName script name (identifier)
   */
  @Override
  public void stopScript(String scriptName) {
    scriptRepository.getScript(scriptName).stopScriptExecution();
    logger.debug("[{}] - Script execution canceled", scriptName);
  }

  /**
   * Method for removing a script from the {@link ScriptRepository}. You cannot delete a script with
   * RUNNING status. Otherwise, an {@link WrongScriptStatusException exception} will be thrown.
   * 
   * @param scriptName script name (identifier)
   * @throws WrongScriptException if script is running
   */
  @Override
  public void deleteScript(String scriptName) {
    final Script script = scriptRepository.getScript(scriptName);
    synchronized (script) {
      if (script.getStatus() == RUNNING)
        throw new WrongScriptStatusException("To delete a running script, you must first stop it",
            script.getStatus());
      scriptRepository.deleteScript(scriptName);
    }
    logger.debug("[{}] - Script deleted from the service", script.getName());
  }

  /**
   * A method for checking a script name for forbidden words and characters
   * 
   * @param scriptName script name (identifier)
   * 
   * @throws WrongNameException if script name contains forbidden words and symbols
   */
  private void checkName(String scriptName) {
    if (!this.correctlyScriptName.matcher(scriptName).matches())
      throw new WrongNameException(
          "The name uses illegal characters or exceeds the allowed length");
    logger.trace("[{}] - Name verification completed successfully", scriptName);
  }

}
