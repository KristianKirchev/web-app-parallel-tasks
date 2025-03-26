package com.krs.service;

import com.krs.dto.TaskDTO;
import com.krs.dto.task.AbstractTask;
import com.krs.service.sort.model.AbstractTaskResult;

import java.util.UUID;

public interface TaskManager {

    void run(AbstractTask task);

    <T extends TaskDTO> AbstractTaskResult checkStatus(UUID uuid);

    Integer getMaxThreads();
}
