package com.krs.service.factory;

import com.krs.dto.SortingDetailsDTO;
import com.krs.dto.TaskDTO;
import com.krs.service.sort.model.TaskType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.RecursiveAction;

@Slf4j
public class RecursiveActionFactory {
    public static RecursiveAction create(TaskType taskType, TaskDTO taskDto) {
        log.info("Task Type: {}", taskType);
        return switch (taskType) {
            case SORT ->
                    SortFactory.create(((SortingDetailsDTO) taskDto).getSortingMethod(), BigDecimal::compareTo, ((SortingDetailsDTO) taskDto).getData());
            default -> throw new IllegalArgumentException("No such task type!");
        };
    }
}
