package com.krs.service;

import com.krs.dto.TaskDTO;

import java.util.UUID;

public interface AwsS3Service {

    void saveTaskDetails(UUID id, TaskDTO taskDTO);

    void updateTaskDetails(UUID id, TaskDTO taskDTO);

    <T> T readTaskDetails(UUID id, Class<T> clazz);
}
