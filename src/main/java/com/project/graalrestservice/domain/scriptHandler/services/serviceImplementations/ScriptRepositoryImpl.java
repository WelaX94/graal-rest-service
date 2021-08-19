package com.project.graalrestservice.domain.scriptHandler.services.serviceImplementations;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.Page;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.domain.scriptHandler.services.ScriptRepository;
import com.project.graalrestservice.domain.scriptHandler.exceptions.PageDoesNotExistException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongNameException;
import com.project.graalrestservice.domain.scriptHandler.exceptions.WrongArgumentException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for working with a list of scripts
 */
@Service
public class ScriptRepositoryImpl implements ScriptRepository {

    private final ConcurrentHashMap<String, Script> map;
    private final List<Character> correctFilters = new ArrayList<>(List.of('q', 'r', 's', 'f', 'c'));

    /**
     * Adds a new script to the map
     * @param scriptName script name (identifier)
     * @param script script info, contains all the information about the script
     * @throws WrongNameException if a script with this name already exists
     */
    @Override
    public void put(String scriptName, Script script) {
        if (map.putIfAbsent(scriptName, script) != null) throw new WrongNameException("Such a name is already in use");
    }

    /**
     * The method returns information about the script you are looking for
     * @param scriptName script name (identifier)
     * @return Script with information about the script
     * @throws ScriptNotFoundException if script not found
     */
    @Override
    public Script get(String scriptName) {
        Script script = map.get(scriptName);
        if (script == null) throw new ScriptNotFoundException(scriptName);
        return script;
    }

    /**
     * A method to get the script list page
     * @param filters filter list
     * @param pageSize page size
     * @param page page number
     * @return Page filtered and sorted by specified parameters
     * @throws WrongArgumentException if page or pageSize less than 1. Also discarded if the list is empty for a given page
     */
    @Override
    public Page<List<ScriptInfoForList>> getScriptListPage(String filters, int pageSize, int page) {
        if (page < 1) throw new WrongArgumentException("The page number cannot be less than 1");
        if (pageSize < 1) throw new WrongArgumentException("The page size cannot be less than 1");

        Set<ScriptInfoForList> scriptSet;
        if (filters.equalsIgnoreCase("basic")) {
            scriptSet = getDefaultSortedScripts();
        } else {
            scriptSet = getFilteredAndSortedScripts(filters);
        }

        int end = page * pageSize;
        int start = end - pageSize;
        int listSize = scriptSet.size();
        if (start >= listSize) throw new PageDoesNotExistException(page);

        int count = 0;
        List<ScriptInfoForList> output = new ArrayList<>(pageSize);
        for (ScriptInfoForList scriptInfoForList: scriptSet) {
            if (count++ < start) continue;
            output.add(scriptInfoForList);
            if (count >= end || count >= listSize) break;
        }

        return new Page<>(output, page, listSize, filters, pageSize);
    }

    /**
     * A method to get a filtered and sorted set of scripts by specified parameters
     * @param filters filter list
     * @return TreeSet of scripts
     */
    private Set<ScriptInfoForList> getFilteredAndSortedScripts(String filters) {
        checkFilter(filters);
        final ScriptStatus.Priority scriptStatusPriority = new ScriptStatus.Priority(filters);
        final Set<ScriptInfoForList> set = new TreeSet<>(
                (s1, s2) -> Comparator
                        .comparing(ScriptInfoForList::returnPriority)
                        .thenComparing(ScriptInfoForList::getCreatedTime)
                        .thenComparing(ScriptInfoForList::getName)
                        .compare(s1,s2));
        OUTER:
        for (Map.Entry<String, Script> entry : map.entrySet()) {
            final char letterStatus = entry.getValue().getScriptStatus().getLetter();
            for (int f = 0; f < filters.length(); f++) {
                if (letterStatus == filters.charAt(f)) {
                    set.add(new ScriptInfoForList(entry.getValue(), scriptStatusPriority));
                    continue OUTER;
                }
            }
        }
        return set;
    }

    /**
     * A method to get a set of filters in default sorted order
     * @return TreeSet of scripts
     */
    private Set<ScriptInfoForList> getDefaultSortedScripts() {
        final Set<ScriptInfoForList> set = new TreeSet<>(
                (s1, s2) -> Comparator
                        .comparing(ScriptInfoForList::returnPriority)
                        .thenComparing(ScriptInfoForList::getCreatedTime)
                        .thenComparing(ScriptInfoForList::getName)
                        .compare(s1,s2));
        for (Map.Entry<String, Script> entry : map.entrySet()) {
            set.add(new ScriptInfoForList(entry.getValue()));
        }
        return set;
    }

    /**
     * Method for checking the validity of the filter list
     * @param filter filter list
     * @throws WrongArgumentException if length of the filter list more than 5 characters or or if there is an invalid filter in the list
     */
    private void checkFilter(String filter) {
        if (filter.length() > 5) throw new WrongArgumentException("The length of the filter can not exceed 5 characters");
        final List<Character> correctFiltersList = new ArrayList<>(correctFilters);
        int correctFilterLength = correctFiltersList.size();
        OUTER:
        for(int f = 0; f < filter.length(); f++) {
            for(int i = 0; i < correctFilterLength; i++) {
                if (filter.charAt(f) == correctFiltersList.get(i)) {
                    correctFiltersList.remove(i);
                    correctFilterLength--;
                    continue OUTER;
                }
            }
            throw new WrongArgumentException(filter.charAt(f));
        }
    }

    /**
     * Method for removing a script from the list
     * @param scriptName script name (identifier)
     * @throws ScriptNotFoundException if script not found
     */
    @Override
    public void delete(String scriptName) {
        if(map.remove(scriptName) == null) throw new ScriptNotFoundException(scriptName);
    }

    public ScriptRepositoryImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    public ScriptRepositoryImpl(ConcurrentHashMap<String, Script> map) {
        this.map = map;
    }

}
