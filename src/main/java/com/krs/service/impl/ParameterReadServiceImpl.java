package com.krs.service.impl;

import com.krs.exception.TaskDoesNotExistException;
import com.krs.repository.TaskRepository;
import com.krs.service.AwsS3Service;
import com.krs.service.ParameterReadService;
import com.krs.service.sort.model.TaskStatus;
import com.krs.service.sort.model.TaskType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ParameterReadServiceImpl implements ParameterReadService {

    private final TaskRepository taskRepository;
    private final AwsS3Service awsS3Service;

    @Override
    public TaskStatus getStatus(UUID uuid) {
        Optional<String> status = taskRepository.findStatusById(uuid);
        if (status.isPresent()) {
            return TaskStatus.fromValue(status.get());
        }
        throw new TaskDoesNotExistException("No task with id: " + uuid);
    }

    @Override
    public Integer getThreadsNum(UUID uuid) {
        Optional<Integer> threadNum = taskRepository.findThreadsById(uuid);
        if (threadNum.isPresent()) {
            return threadNum.get();
        }
        throw new TaskDoesNotExistException("No task with id: " + uuid);
    }

    @Override
    public TaskType getTaskType(UUID uuid) {
        Optional<String> taskType = taskRepository.findTaskTypeById(uuid);
        if (taskType.isPresent()) {
            return TaskType.fromValue(taskType.get());
        }
        throw new TaskDoesNotExistException("No task with id: " + uuid);
    }

    @Override
    public <T> T readTaskDetails(UUID uuid, Class<T> clazz) {
        return awsS3Service.readTaskDetails(uuid, clazz);
    }
}
