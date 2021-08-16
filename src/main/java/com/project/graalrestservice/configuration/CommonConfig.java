package com.project.graalrestservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration class
 */
@Configuration
@EnableAsync
public class CommonConfig {

    private final int corePoolSize;

    public CommonConfig(@Value("${scripts.executor.corePoolSize}") int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * Method for creating executor service bean
     * @return executor service
     */
    @Bean
    public ExecutorService threadPoolTaskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }

    /**
     * Method for swagger2 configuration
     * @return Docket configuration
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

}
