package com.project.graalrestservice.app.configuration;

import com.project.graalrestservice.web.util.StringToScriptStatusConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMVC configuration class
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final int corePoolSize;
  private final int maxPoolSize;
  private final int queueCapacity;
  private final String threadNamePrefix;

  /**
   * Basic constructor
   * 
   * @param corePoolSize initial thread pool size
   * @param maxPoolSize maximum thread pool size
   * @param queueCapacity the size of the queue, above which the size of the pool will increase to
   *        the maximum
   * @param threadNamePrefix prefix for the name of new threads
   */
  public WebMvcConfig(@Value("${webmvc.executor.corePoolSize}") int corePoolSize,
      @Value("${webmvc.executor.maxPoolSize}") int maxPoolSize,
      @Value("${webmvc.executor.queueCapacity}") int queueCapacity,
      @Value("${webmvc.executor.threadNamePrefix}") String threadNamePrefix) {
    this.corePoolSize = corePoolSize;
    this.maxPoolSize = maxPoolSize;
    this.queueCapacity = queueCapacity;
    this.threadNamePrefix = threadNamePrefix;
  }

  /**
   * Configuration for custom ThreadPoolTaskExecutor
   */
  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.initialize();
    configurer.setTaskExecutor(executor);
  }

  /**
   * Registration {@link StringToScriptStatusConverter custom converter}
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToScriptStatusConverter());
  }

}
