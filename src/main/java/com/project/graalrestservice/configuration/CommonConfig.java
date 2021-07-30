package com.project.graalrestservice.configuration;

import com.project.graalrestservice.models.ScriptInfo;
import com.project.graalrestservice.repositories.ScriptHandler;
import com.project.graalrestservice.repositories.ScriptList;
import com.project.graalrestservice.services.ScriptHandlerService;
import com.project.graalrestservice.services.ScriptListService;
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
        return new ScriptHandlerService();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public ScriptList scriptList() {
        return new ScriptListService();
    }

}
