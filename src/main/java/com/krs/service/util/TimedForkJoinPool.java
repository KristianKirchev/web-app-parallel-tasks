package com.krs.service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

@Slf4j
public class TimedForkJoinPool extends ForkJoinPool {

    public TimedForkJoinPool(int parallelism) {
        super(parallelism);
    }

    public long timedInvoke(UUID taskId, RecursiveAction recursiveAction) {
        log.info("Timer started, taskId: {}", taskId);
        long time = System.currentTimeMillis();
        invoke(recursiveAction);
        time = System.currentTimeMillis() - time;
        log.info("Timer stopped, taskId: {}", taskId);

        return time;
    }
}