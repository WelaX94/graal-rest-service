package com.project.graalrestservice.convert;

import com.project.graalrestservice.domain.scriptHandler.enums.ScriptStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToScriptStatusConverter implements Converter<String, ScriptStatus> {

  @Override
  public ScriptStatus convert(String source) {
    return ScriptStatus.valueOf(source.toUpperCase());
  }

}
