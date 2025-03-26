package com.krs.service.impl;

import com.krs.dto.TaskDTO;
import com.krs.dto.task.AbstractTask;
import com.krs.entity.GeneralTaskInfo;
import com.krs.repository.TaskRepository;
import com.krs.service.AwsS3Service;
import com.krs.service.ParameterStoreService;
import com.krs.service.factory.TaskDtoFactory;
import com.krs.service.sort.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParameterStoreServiceImpl implements ParameterStoreService {

    private final TaskRepository taskRepository;
    private final AwsS3Service awsS3Service;


    @Override
    public void storeTaskDetails(AbstractTask abstractTask) {
        awsS3Service.saveTaskDetails(abstractTask.getTaskId(), TaskDtoFactory.createTaskResult(abstractTask));
        taskRepository.save(new GeneralTaskInfo(abstractTask.getTaskId(),
                TaskStatus.PENDING.getValue(),
                abstractTask.getNumberOfThreads(),
                abstractTask.getTaskType().getValue()));
    }

    @Override
    public void updateTaskDetails(UUID taskId, TaskDTO taskDTO) {
        awsS3Service.updateTaskDetails(taskId, taskDTO);
    }

    @Override
    public void updateStatus(UUID taskId, TaskStatus taskStatus) {
        taskRepository.updateTaskStatus(taskId, taskStatus.getValue());
    }
}
