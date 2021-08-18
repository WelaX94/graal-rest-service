package com.project.graalrestservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration class
 */
@Configuration
@EnableAsync
public class CommonConfig {

    /**
     * Method for swagger2 configuration
     *
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

    /**
     * Custom spring thread pool task executor
     *
     * @param threadNamePrefix prefix for the name of new threads
     * @param corePoolSize     initial thread pool size
     * @param maxPoolSize      maximum thread pool size
     * @param queueCapacity    the size of the queue, above which the size of the pool will increase to the maximum
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor threadPoolTaskExecutor(
            @Value("${scripts.executor.threadNamePrefix}") String threadNamePrefix,
            @Value("${scripts.executor.corePoolSize}") int corePoolSize,
            @Value("${scripts.executor.maxPoolSize}") int maxPoolSize,
            @Value("${scripts.executor.queueCapacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

    /**
     * Zalando problem configuration bean
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(
                new ProblemModule(),
                new ConstraintViolationProblemModule());
    }

}
