package com.krs.dto.task;

import com.krs.service.sort.model.TaskType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@Getter
@RequiredArgsConstructor
public abstract class AbstractTask {

    protected final UUID taskId;
    protected final int numberOfThreads;
    protected final TaskType taskType;
}
