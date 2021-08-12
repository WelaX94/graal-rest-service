package com.project.graalrestservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan("com.project.graalrestservice")
@PropertySource("classpath:application.properties")
@EnableAsync
public class CommonConfig {

    @Value("${executor.corePoolSize}")
    private int corePoolSize;
    @Value("${executor.maxPoolSize}")
    private int maxPoolSize;
    @Value("${executor.queueCapacity}")
    private int queueCapacity;

    @Value("${executor.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean
    public ExecutorService threadPoolTaskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }

}
