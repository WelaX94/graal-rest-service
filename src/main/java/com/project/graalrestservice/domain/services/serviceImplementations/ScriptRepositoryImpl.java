package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.enums.ScriptStatus;
import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.models.representation.ScriptListPage;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongArgumentException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptRepositoryImpl implements ScriptRepository {

    private final ConcurrentHashMap<String, ScriptInfo> map;
    private final List<Character> correctFilters = new ArrayList<>(List.of('q', 'r', 's', 'f', 'c'));

    @Override
    public void put(String scriptName, ScriptInfo scriptInfo) {
        if (map.putIfAbsent(scriptName, scriptInfo) != null) throw new WrongNameException("Such a name is already in use");
    }

    @Override
    public ScriptInfo get(String scriptName) {
        ScriptInfo scriptInfo = map.get(scriptName);
        if (scriptInfo == null) throw new ScriptNotFoundException(scriptName);
        return scriptInfo;
    }

    @Override
    public ScriptListPage getScriptListPage(String filters, Integer pageSize, Integer page) {
        if (page == null) {
            page = 1;
        } else {
            if (page < 1) throw new WrongArgumentException("The page number cannot be less than 1");
        }

        if (pageSize == null) {
            pageSize = 10;
        } else {
            if (pageSize < 1) throw new WrongArgumentException("The page size cannot be less than 1");
        }

        Set<ScriptInfoForList> scriptSet;
        if (filters == null || filters.equalsIgnoreCase("basic")) {
            filters = "basic";
            scriptSet = getDefaultSortedScripts();
        } else {
            scriptSet = getFilteredAndSortedScripts(filters);
        }

        int end = page * pageSize;
        int start = end - pageSize;
        int listSize = scriptSet.size();
        if (start >= listSize) throw new WrongArgumentException(page);

        int count = 0;
        List<ScriptInfoForList> output = new ArrayList<>(pageSize);
        for (ScriptInfoForList scriptInfoForList: scriptSet) {
            if (count++ < start) continue;
            output.add(scriptInfoForList);
            if (count >= end || count >= listSize) break;
        }

        return new ScriptListPage(output, page, listSize, filters, pageSize);
    }

    private Set<ScriptInfoForList> getFilteredAndSortedScripts(String filters) {
        checkFilter(filters);
        final ScriptStatus.Priority scriptStatusPriority = new ScriptStatus.Priority(filters);
        final Set<ScriptInfoForList> set = new TreeSet<>();
        OUTER:
        for (Map.Entry<String, ScriptInfo> entry : map.entrySet()) {
            final char letterStatus = entry.getValue().getScriptStatus().getLetter();
            for (int f = 0; f < filters.length(); f++) {
                if (letterStatus == filters.charAt(f)) {
                    set.add(new ScriptInfoForList(entry.getKey(), entry.getValue(), scriptStatusPriority));
                    continue OUTER;
                }
            }
        }
        return set;
    }

    private Set<ScriptInfoForList> getDefaultSortedScripts() {
        final Set<ScriptInfoForList> set = new TreeSet<>();
        for (Map.Entry<String, ScriptInfo> entry : map.entrySet()) {
            set.add(new ScriptInfoForList(entry.getKey(), entry.getValue()));
        }
        return set;
    }

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

    @Override
    public void delete(String scriptName) {
        if(map.remove(scriptName) == null) throw new ScriptNotFoundException(scriptName);
    }

    public ScriptRepositoryImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    public ScriptRepositoryImpl(ConcurrentHashMap<String, ScriptInfo> map) {
        this.map = map;
    }

}
