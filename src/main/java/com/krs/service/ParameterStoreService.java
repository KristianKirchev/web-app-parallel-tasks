package com.krs.service;

import com.krs.dto.TaskDTO;
import com.krs.dto.task.AbstractTask;
import com.krs.service.sort.model.TaskStatus;

import java.util.UUID;

public interface ParameterStoreService {

    void storeTaskDetails(AbstractTask abstractTask);

    void updateTaskDetails(UUID taskId, TaskDTO taskDTO);

    void updateStatus(UUID taskId, TaskStatus taskStatus);
}
