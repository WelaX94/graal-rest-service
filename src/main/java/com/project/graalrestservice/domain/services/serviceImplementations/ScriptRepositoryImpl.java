package com.project.graalrestservice.domain.services.serviceImplementations;

import com.project.graalrestservice.domain.enums.ScriptStatusPriority;
import com.project.graalrestservice.domain.models.ScriptInfo;
import com.project.graalrestservice.domain.models.representation.ScriptInfoForList;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.exceptionHandling.exceptions.ScriptNotFoundException;
import com.project.graalrestservice.exceptionHandling.exceptions.UnknownFilterException;
import com.project.graalrestservice.exceptionHandling.exceptions.WrongNameException;
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
    public Set<ScriptInfoForList> getAllScripts(char ... filters) {
        if (filters.length == 0) return getNonFilteredScripts();
        else return getFilteredScripts(filters);
    }

    @Override
    public List<ScriptInfoForList> getPageScripts(char[] filters, int page) {
        if (page < 0) throw new IllegalArgumentException("The page number cannot be negative");
        List<ScriptInfoForList> list = new ArrayList<>(getFilteredScripts(filters));
        int end = (page + 1) * 10;
        List<ScriptInfoForList> output = new ArrayList<>(10);
        for (int start = end - 10; start < end && start < list.size(); start++) {
            output.add(list.get(start));
        }
        return output;
    }

    private Set<ScriptInfoForList> getFilteredScripts(char[] filters) {
        checkFilter(filters);
        ScriptStatusPriority scriptStatusPriority = new ScriptStatusPriority(filters);
        final Set<ScriptInfoForList> set = new TreeSet<>();
        OUTER:
        for (Map.Entry<String, ScriptInfo> entry : map.entrySet()) {
            final char letterStatus = entry.getValue().getScriptStatus().getLetter();
            for (char filter: filters) {
                if (letterStatus == filter) {
                    set.add(new ScriptInfoForList(entry.getKey(), entry.getValue(), scriptStatusPriority));
                    continue OUTER;
                }
            }
        }
        return set;
    }

    private Set<ScriptInfoForList> getNonFilteredScripts() {
        final Set<ScriptInfoForList> set = new TreeSet<>();
        for (Map.Entry<String, ScriptInfo> entry : map.entrySet()) {
            set.add(new ScriptInfoForList(entry.getKey(), entry.getValue()));
        }
        return set;
    }

    private void checkFilter(char[] filter) {
        if (filter.length > 5) throw new UnknownFilterException("The length of the filter can not exceed 5 characters");
        final List<Character> correctFiltersList = new ArrayList<>(correctFilters);
        int correctFilterLength = correctFiltersList.size();
        OUTER:
        for(char f: filter) {
            for(int i = 0; i < correctFilterLength; i++) {
                if (f == correctFiltersList.get(i)) {
                    correctFiltersList.remove(i);
                    correctFilterLength--;
                    continue OUTER;
                }
            }
            throw new UnknownFilterException("" + f);
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
