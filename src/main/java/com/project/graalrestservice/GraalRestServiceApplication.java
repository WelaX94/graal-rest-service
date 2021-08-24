package com.project.graalrestservice;

import org.graalvm.polyglot.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * Class with main method
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class GraalRestServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(GraalRestServiceApplication.class);

  /**
   * Main method. It also checks for JS Engine. If it is missing, the service does not start.
   */
  public static void main(String[] args) {
    boolean jsDetected = false;
    Context context = Context.newBuilder().build();
    for (String s : context.getEngine().getLanguages().keySet()) {
      if (s.equals("js")) {
        jsDetected = true;
        break;
      }
    }
    context.close();
    if (jsDetected) {
      logger.info("JS Engine detected. The launch of the service begins");
      SpringApplication.run(GraalRestServiceApplication.class, args);
    } else {
      logger.error("JS Engine not found. Service can't start");
    }
  }

}
