package com.project.graalrestservice.configuration;

import com.project.graalrestservice.domain.services.ScriptHandler;
import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptHandlerImpl;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan("com.project.graalrestservice")
@PropertySource("classpath:application.properties")
public class CommonConfig {

    @Bean
    public ScriptHandler scriptHandler() {
        return new ScriptHandlerImpl();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public ScriptService scriptList() {
        return new ScriptServiceImpl();
    }

}
