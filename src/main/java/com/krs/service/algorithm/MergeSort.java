package com.krs.service.algorithm;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Accessors(chain = true)
@Slf4j
public class MergeSort<T extends Number> extends RecursiveAction {

    private final Comparator<T> comparator;
    private List<T> array;

    private void merge(List<T> left, List<T> right) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) < 0)
                array.set(k++, left.get(i++));
            else
                array.set(k++, right.get(j++));
        }
        while (i < left.size()) {
            array.set(k++, left.get(i++));
        }
        while (j < right.size()) {
            array.set(k++, right.get(j++));
        }
    }

    @Override
    protected void compute() {
        if (array.size() < 2)
            return;

        int mid = array.size() / 2;

        List<T> left = new ArrayList<>(array.subList(0, mid));

        List<T> right = new ArrayList<>(array.subList(mid, array.size()));

        invokeAll(new MergeSort<>(comparator, left), new MergeSort<>(comparator, right));
        merge(left, right);
    }
}
