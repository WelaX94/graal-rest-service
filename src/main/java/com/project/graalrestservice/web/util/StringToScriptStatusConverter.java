package com.project.graalrestservice.web.util;

import com.project.graalrestservice.domain.script.enumeration.ScriptStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToScriptStatusConverter implements Converter<String, ScriptStatus> {

  @Override
  public ScriptStatus convert(String source) {
    return ScriptStatus.valueOf(source.toUpperCase());
  }

}
