package com.project.graalrestservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMVC configuration class
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${webmvc.executor.corePoolSize}")
    private int corePoolSize;
    @Value("${webmvc.executor.maxPoolSize}")
    private int maxPoolSize;
    @Value("${webmvc.executor.queueCapacity}")
    private int queueCapacity;

    @Value("${webmvc.executor.threadNamePrefix}")
    private String threadNamePrefix;

    /**
     * A method for creating a custom executor
     * @param configurer AsyncSupportConfigurer
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

}
