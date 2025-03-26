package com.krs.service.algorithm;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Accessors(chain = true)
@Slf4j
public class ShellSort<T extends Number> extends RecursiveAction {

    private final Comparator<T> comparator;
    private List<T> array;

    @Override
    protected void compute() {
        int n = array.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            invokeAll(createTasks(gap));
        }
    }

    private RecursiveAction[] createTasks(int gap) {
        RecursiveAction[] tasks = new RecursiveAction[gap];
        for (int i = 0; i < gap; i++) {
            final int start = i;
            tasks[i] = new RecursiveAction() {
                @Override
                protected void compute() {
                    sort(start, gap);
                }
            };
        }
        return tasks;
    }

    private void sort(int start, int gap) {
        for (int i = start + gap; i < array.size(); i += gap) {
            T temp = array.get(i);
            int j;

            for (j = i; j >= gap && comparator.compare(array.get(j - gap), temp) > 0; j -= gap) {
                array.set(j, array.get(j - gap));
            }

            array.set(j, temp);
        }
    }
}