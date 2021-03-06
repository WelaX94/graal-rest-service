package com.project.graalrestservice.domain.script.service.service_implementation;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import com.project.graalrestservice.domain.script.model.Script;
import com.project.graalrestservice.domain.script.service.ScriptRepository;
import com.project.graalrestservice.domain.script.exception.ScriptNotFoundException;
import com.project.graalrestservice.domain.script.exception.WrongNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A class for working with a list of scripts
 */
@Service
public class ScriptRepositoryImpl implements ScriptRepository {

  private static final Logger logger = LoggerFactory.getLogger(ScriptRepository.class); // NOSONAR
  private final ConcurrentHashMap<String, Script> map = new ConcurrentHashMap<>();

  /**
   * Adds a new script to the map
   *
   * @param scriptName script name (identifier)
   * @param script script info, contains all the information about the script
   * @throws WrongNameException if a script with this name already exists
   */
  @Override
  public void putScript(String scriptName, Script script) {
    if (this.map.putIfAbsent(scriptName, script) != null)
      throw new WrongNameException("Such a name is already in use");
    logger.trace("[{}] - Script added to the script repository", scriptName);
  }

  /**
   * The method returns information about the script you are looking for
   *
   * @param scriptName script name (identifier)
   * @return Script with information about the script
   * @throws ScriptNotFoundException if script not found
   */
  @Override
  public Script getScript(String scriptName) {
    Script script = this.map.get(scriptName);
    if (script == null)
      throw new ScriptNotFoundException(scriptName);
    logger.trace("[{}] - Script repository return the script", scriptName);
    return script;
  }

  /**
   * The method returns a filtered list with the specified parameters. If there are no filters
   * (null), it returns all the scripts present in the repository ({@link #map in map})
   *
   * @param scriptStatus allows you to specify the scripts with which statuses you are interested in
   *        the output. If null - scripts with all statuses will be displayed.
   * @param nameContains it allows you to specify an expression that must contain the name of the
   *        script. If null - scripts with all names will be displayed
   * @return filtered {@link List} of {@link Script}
   */
  @Override
  public List<Script> getScriptList(ScriptStatus scriptStatus, String nameContains) {
    boolean nullableName = nameContains == null;
    boolean nullableStatus = scriptStatus == null;
    List<Script> scriptList =
        this.map.values().parallelStream()
            .filter(s -> (nullableStatus || s.getStatus() == scriptStatus)
                && (nullableName || s.getName().contains(nameContains)))
            .collect(Collectors.toList());
    logger.trace(
        "Script repository return filtered script list. Parameters [scriptStatus={}, nameContains={}]",
        scriptStatus, nameContains);
    return scriptList;
  }

  /**
   * Method for removing a script from the list.
   *
   * @param scriptName script name (identifier)
   * @throws ScriptNotFoundException if script not found
   */
  @Override
  public void deleteScript(String scriptName) {
    Script script = this.map.remove(scriptName);
    if (script == null)
      throw new ScriptNotFoundException(scriptName);
    script.cancelExecution();
    logger.trace("[{}] - Script deleted from script repository", scriptName);
  }

}
