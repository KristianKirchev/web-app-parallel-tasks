package com.krs.service;

import com.krs.service.sort.model.TaskStatus;
import com.krs.service.sort.model.TaskType;

import java.util.UUID;

public interface ParameterReadService {

    TaskStatus getStatus(UUID uuid);

    Integer getThreadsNum(UUID uuid);

    TaskType getTaskType(UUID uuid);

    <T> T readTaskDetails(UUID uuid, Class<T> clazz);
}
