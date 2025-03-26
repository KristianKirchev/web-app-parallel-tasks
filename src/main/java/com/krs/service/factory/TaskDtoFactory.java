package com.krs.service.factory;

import com.krs.dto.SortingDetailsDTO;
import com.krs.dto.TaskDTO;
import com.krs.dto.task.AbstractTask;
import com.krs.dto.task.SortTask;

public class TaskDtoFactory {

    private static SortingDetailsDTO createSortTaskResult(AbstractTask abstractTask) {
        return new SortingDetailsDTO((SortTask) abstractTask);
    }

    public static TaskDTO createTaskResult(AbstractTask abstractTask) {

        return switch (abstractTask.getTaskType()) {
            case SORT -> createSortTaskResult(abstractTask);
            default -> throw new IllegalArgumentException("Unsupported task type: " + abstractTask.getTaskType());
        };
    }
}
