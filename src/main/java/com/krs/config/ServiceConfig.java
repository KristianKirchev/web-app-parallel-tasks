package com.krs.config;

import com.krs.repository.TaskRepository;
import com.krs.service.AwsS3Service;
import com.krs.service.ParameterReadService;
import com.krs.service.ParameterStoreService;
import com.krs.service.TaskManager;
import com.krs.service.TaskOrchestrator;
import com.krs.service.impl.ParameterReadServiceImpl;
import com.krs.service.impl.ParameterStoreServiceImpl;
import com.krs.service.impl.TaskManagerImpl;
import com.krs.service.impl.TaskOrchestratorImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ServiceConfig {

    @Bean
    @ConfigurationProperties(prefix = "app")
    @Validated
    ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }

    @Bean
    TaskManager taskManager(ParameterStoreService parameterStoreService, ParameterReadService parameterReadService, TaskOrchestrator taskOrchestrator, ApplicationProperties applicationProperties) {
        return new TaskManagerImpl(parameterStoreService, parameterReadService, taskOrchestrator, applicationProperties.getMaxThreads());
    }

    @Bean
    ParameterStoreService parameterStoreService(TaskRepository taskRepository, AwsS3Service awsS3Service) {
        return new ParameterStoreServiceImpl(taskRepository, awsS3Service);
    }

    @Bean
    ParameterReadService parameterReadService(TaskRepository taskRepository, AwsS3Service awsS3Service) {
        return new ParameterReadServiceImpl(taskRepository, awsS3Service);
    }

    @Bean
    TaskOrchestrator taskOrchestrator(ParameterStoreService parameterStoreService, ParameterReadService parameterReadService, ApplicationProperties applicationProperties) {
        return new TaskOrchestratorImpl(parameterStoreService, parameterReadService, new ConcurrentLinkedQueue<>(), new AtomicBoolean(false), new AtomicInteger(applicationProperties.getMaxThreads()));
    }
}
