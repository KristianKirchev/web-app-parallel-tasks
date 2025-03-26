package com.krs.service.impl;

import com.krs.dto.SortingDetailsDTO;
import com.krs.dto.TaskDTO;
import com.krs.dto.task.AbstractTask;
import com.krs.service.ParameterReadService;
import com.krs.service.ParameterStoreService;
import com.krs.service.TaskManager;
import com.krs.service.TaskOrchestrator;
import com.krs.service.factory.TaskResultFactory;
import com.krs.service.sort.model.AbstractTaskResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class TaskManagerImpl implements TaskManager {

    @Getter(AccessLevel.NONE)
    private final ParameterStoreService parameterStoreService;

    @Getter(AccessLevel.NONE)
    private final ParameterReadService parameterReadService;

    @Getter(AccessLevel.NONE)
    private final TaskOrchestrator taskOrchestrator;

    private final Integer maxThreads;

    @Override
    public void run(AbstractTask task) {

        parameterStoreService.storeTaskDetails(task);

        taskOrchestrator.addTask(task.getTaskId());
    }

    @Override
    public <T extends TaskDTO> AbstractTaskResult checkStatus(UUID uuid) {
        Class<T> clazz;

        switch (parameterReadService.getTaskType(uuid)) {
            case SORT -> clazz = (Class<T>) SortingDetailsDTO.class;
            default -> throw new IllegalArgumentException("Illegal task type");
        }

        return TaskResultFactory.createTaskResult(parameterReadService.getTaskType(uuid).getValue(), parameterReadService.getStatus(uuid), parameterReadService.readTaskDetails(uuid, clazz));
    }
}
