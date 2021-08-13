package com.project.graalrestservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration class
 */
@Configuration
@EnableAsync
public class CommonConfig {

    @Value("${scripts.executor.corePoolSize}")
    private int corePoolSize;

    /**
     * Method for creating executor service bean
     * @return executor service
     */
    @Bean
    public ExecutorService threadPoolTaskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }

}
