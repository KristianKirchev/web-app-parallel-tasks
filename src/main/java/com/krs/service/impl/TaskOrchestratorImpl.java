package com.krs.service.impl;

import com.krs.dto.SortingDetailsDTO;
import com.krs.dto.TaskDTO;
import com.krs.service.ParameterReadService;
import com.krs.service.ParameterStoreService;
import com.krs.service.TaskOrchestrator;
import com.krs.service.factory.RecursiveActionFactory;
import com.krs.service.sort.model.TaskStatus;
import com.krs.service.util.TimedForkJoinPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class TaskOrchestratorImpl implements TaskOrchestrator {

    private final ParameterStoreService parameterStoreService;
    private final ParameterReadService parameterReadService;
    private final Queue<UUID> taskQueue;
    private final AtomicBoolean isOrchestratorActive;
    private final AtomicInteger availableThreads;

    @Override
    public void addTask(UUID taskId) {
        log.info("Orchestrator, adding task to queue with Id: {}", taskId);
        taskQueue.add(taskId);
        startOrchestrator();
    }

    private void startOrchestrator() {
        log.info("Orchestrator started...");
        if (isOrchestratorActive.compareAndSet(false, true)) {
            log.info("Executing processTasks()");
            CompletableFuture.runAsync(this::processTasks);
        }
    }

    private void processTasks() {
        while (!taskQueue.isEmpty()) {
            log.info("Current queue size: {}", taskQueue.size());
            UUID taskId = taskQueue.peek();
            int requiredThreads = parameterReadService.getThreadsNum(taskId);

            log.info("Current task with Id: {}, threads: {}", taskId, requiredThreads);

            if (availableThreads.get() < requiredThreads) {
                log.warn("Not enough resources, taskId: {}", taskId);

                break;
            }

            availableThreads.addAndGet(-requiredThreads);
            log.info("Enough resources for task with Id: {}", taskId);
            log.info("Threads left: {}", availableThreads);
            taskQueue.poll();

            log.info("Executing task: {}", taskId);

            TimedForkJoinPool timedForkJoinPool = new TimedForkJoinPool(requiredThreads);

            CompletableFuture.runAsync(() -> {

                log.info("Started sorting array, taskId: {}", taskId);

                Class<? extends TaskDTO> clazz;

                switch (parameterReadService.getTaskType(taskId)) {
                    case SORT -> clazz = SortingDetailsDTO.class;
                    default -> throw new IllegalArgumentException("Illegal task type");
                }

                TaskDTO taskDto = parameterReadService.readTaskDetails(taskId, clazz);
                RecursiveAction recursiveAction = RecursiveActionFactory.create(parameterReadService.getTaskType(taskId), taskDto);
                parameterStoreService.updateStatus(taskId, TaskStatus.IN_PROGRESS);
                ((SortingDetailsDTO) taskDto).setSortingTimeMillis(timedForkJoinPool.timedInvoke(taskId, recursiveAction));

                log.info("Finished sorting array, taskId: {}", taskId);
                parameterStoreService.updateStatus(taskId, TaskStatus.COMPLETED);
                parameterStoreService.updateTaskDetails(taskId, taskDto);

            }).handle((result, exception) -> {
                log.info("Completing task with id: {}", taskId);
                timedForkJoinPool.shutdown();
                taskCompleted(requiredThreads);
                try {
                    if (!timedForkJoinPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        timedForkJoinPool.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    log.warn("Shutting down problem, taskId: {}", taskId);
                    timedForkJoinPool.shutdownNow();
                    Thread.currentThread().interrupt();
                }

                return null;
            });
        }
        log.info("Deactivating orchestrator");
        isOrchestratorActive.set(false);
    }

    private synchronized void taskCompleted(int releasedThreads) {
        availableThreads.addAndGet(releasedThreads);
        log.info("New available threads: {}", availableThreads);
        if (!taskQueue.isEmpty()) {
            log.info("Restarting orchestrator");
            startOrchestrator();
        }
    }
}
