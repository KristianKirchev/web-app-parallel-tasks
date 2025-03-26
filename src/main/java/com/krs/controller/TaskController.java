package com.krs.controller;

import com.krs.dto.task.AbstractTask;
import com.krs.service.TaskManager;
import com.krs.service.factory.TaskFactory;
import com.krs.service.sort.api.TaskApi;
import com.krs.service.sort.model.AbstractTaskResult;
import com.krs.service.sort.model.CheckStatus200Response;
import com.krs.service.sort.model.PostTaskRequest;
import com.krs.service.sort.model.SubmitTask202Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskManager taskManager;

    @Override
    public ResponseEntity<CheckStatus200Response> checkStatus(UUID taskId) {
        log.info("Getting response for task with Id: {}", taskId);

        AbstractTaskResult taskResult = taskManager.checkStatus(taskId);

        CheckStatus200Response taskStatusResponse = new CheckStatus200Response()
                .taskResult(taskResult);

        return ResponseEntity.status(HttpStatus.OK).body(taskStatusResponse);
    }

    @Override
    public ResponseEntity<SubmitTask202Response> submitTask(@Valid PostTaskRequest postTaskRequest) {
        log.info("Validating and converting request to SortTask...");

        AbstractTask sortTask = TaskFactory.createTask(postTaskRequest, taskManager.getMaxThreads());
        taskManager.run(sortTask);

        log.info("Submitting task with Id: {}", sortTask.getTaskId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmitTask202Response(sortTask.getTaskId()));
    }
}