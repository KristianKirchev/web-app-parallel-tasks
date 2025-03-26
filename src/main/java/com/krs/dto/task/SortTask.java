package com.krs.dto.task;

import com.krs.service.sort.model.AlgorithmType;
import com.krs.service.sort.model.TaskType;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class SortTask extends AbstractTask {

    private final AlgorithmType algorithmType;
    private final List<BigDecimal> unsortedArray;

    public SortTask(TaskType taskType, UUID taskId, int numberOfThreads, AlgorithmType algorithmType, List<BigDecimal> unsortedArray) {
        super(taskId, numberOfThreads, taskType);
        this.algorithmType = algorithmType;
        this.unsortedArray = unsortedArray;
    }
}