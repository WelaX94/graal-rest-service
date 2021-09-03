package com.project.graalrestservice.app.configuration;

import com.project.graalrestservice.web.util.StringToScriptStatusConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMVC configuration class
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * Registration {@link StringToScriptStatusConverter custom converter}
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToScriptStatusConverter());
  }

}
