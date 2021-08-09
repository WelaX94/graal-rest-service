package com.project.graalrestservice.configuration;

import com.project.graalrestservice.domain.services.ScriptService;
import com.project.graalrestservice.domain.services.ScriptRepository;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptServiceImpl;
import com.project.graalrestservice.domain.services.serviceImplementations.ScriptRepositoryImpl;
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
    public ScriptService scriptHandler() {
        return new ScriptServiceImpl();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public ScriptRepository scriptList() {
        return new ScriptRepositoryImpl();
    }

}
