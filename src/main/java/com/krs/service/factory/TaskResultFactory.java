package com.krs.service.factory;

import com.krs.dto.SortingDetailsDTO;
import com.krs.dto.TaskDTO;
import com.krs.service.sort.model.AbstractTaskResult;
import com.krs.service.sort.model.SortTaskResult;
import com.krs.service.sort.model.TaskStatus;
import com.krs.service.sort.model.TaskType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskResultFactory {

    private static SortTaskResult createSortTaskResult(TaskStatus taskStatus, TaskDTO taskDTO) {
        return new SortTaskResult()
                .taskType(TaskType.SORT)
                .taskStatus(taskStatus)
                .sortedArray(((SortingDetailsDTO) taskDTO).getData())
                .sortTimeMillis(((SortingDetailsDTO) taskDTO).getSortingTimeMillis());
    }

    public static AbstractTaskResult createTaskResult(String taskType, TaskStatus taskStatus, TaskDTO taskDTO) {

        log.info("Creating Task Result, taskType: {}-{}, taskStatus: {}", taskType, TaskType.fromValue(taskType), taskStatus);

        return switch (TaskType.fromValue(taskType)) {
            case SORT -> createSortTaskResult(taskStatus, taskDTO);
            default -> throw new IllegalArgumentException("Unsupported task type: " + taskType);
        };
    }
}
