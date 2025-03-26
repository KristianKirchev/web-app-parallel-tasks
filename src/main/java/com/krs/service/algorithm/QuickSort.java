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
public class QuickSort<T extends Number> extends RecursiveAction {

    private final Comparator<T> comparator;
    private List<T> array;
    private int left;
    private int right;

    public QuickSort(Comparator<T> comparator, List<T> array) {
        this.comparator = comparator;
        this.array = array;
        this.left = 0;
        this.right = array.size() - 1;
    }

    @Override
    protected void compute() {
        if (left < right) {
            int pivot = partition(array, left, right);
            invokeAll(new QuickSort<>(comparator, array, left, pivot),
                    new QuickSort<>(comparator, array, pivot + 1, right));
        }
    }

    private int partition(List<T> array, int low, int high) {
        T pivot = array.get(low);
        int i = low - 1;
        int j = high + 1;
        while (true) {
            do {
                i++;
            } while (comparator.compare(array.get(i), pivot) < 0);

            do {
                j--;
            } while (comparator.compare(array.get(j), pivot) > 0);

            if (i >= j)
                return j;

            swap(array, i, j);
        }
    }

    private void swap(List<T> array, int i, int j) {
        T temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
    }
}