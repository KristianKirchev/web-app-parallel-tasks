package com.krs.service.factory;

import com.krs.service.algorithm.MergeSort;
import com.krs.service.algorithm.QuickSort;
import com.krs.service.algorithm.ShellSort;
import com.krs.service.sort.model.AlgorithmType;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@Slf4j
public class SortFactory {

    public static <T extends Number> RecursiveAction create(AlgorithmType alg, Comparator<T> comparator, List<T> array) {
        log.info("Array size: {}", array.size());
        switch (alg) {
            case SHELL -> {
                return new ShellSort<>(comparator, array);
            }
            case QUICK -> {
                return new QuickSort<>(comparator, array);
            }
            case MERGE -> {
                return new MergeSort<>(comparator, array);
            }
            default -> throw new IllegalArgumentException("Unknown sorting algorithm: " + alg);
        }
    }
}
