package com.project.graalrestservice.configuration;

import com.project.graalrestservice.repositories.ScriptExecutor;
import com.project.graalrestservice.services.ScriptExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.project.graalrestservice")
@PropertySource("classpath:application.properties")
public class CommonConfig {

    @Bean
    public ScriptExecutor scriptExecutor() {
        return new ScriptExecutorService();
    }

}
