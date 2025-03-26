package com.krs.service.factory;

import com.krs.dto.task.AbstractTask;
import com.krs.dto.task.SortTask;
import com.krs.exception.InvalidThreadNumberException;
import com.krs.service.sort.model.AlgorithmType;
import com.krs.service.sort.model.PostTaskRequest;
import com.krs.service.sort.model.TaskType;

import java.util.Objects;
import java.util.UUID;

public class TaskFactory {

    private static SortTask createSortTask(PostTaskRequest postTaskRequest, Integer maxThreads) {
        int numberOfThreads = Objects.requireNonNullElse(postTaskRequest.getNumberOfThreads(), 2);
        AlgorithmType algorithmType = Objects.requireNonNullElse(postTaskRequest.getAlgorithmType(), AlgorithmType.MERGE);

        if (numberOfThreads < 1) {
            throw new InvalidThreadNumberException("Number of threads must be at least 1, but was: " + numberOfThreads);
        }
        if (numberOfThreads > maxThreads) {
            throw new InvalidThreadNumberException("Number of threads: " + numberOfThreads + ", exceeds the maximum: " + maxThreads);
        }

        UUID uuid = UUID.randomUUID();
        return new SortTask(TaskType.SORT, uuid, numberOfThreads, algorithmType, postTaskRequest.getUnsortedArray());
    }

    public static AbstractTask createTask(PostTaskRequest postTaskRequest, Integer maxThreads) {
        TaskType taskType = Objects.requireNonNull(postTaskRequest.getTaskType());

        switch (taskType) {
            case SORT -> {
                return createSortTask(postTaskRequest, maxThreads);
            }
            default -> throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }
    }
}
