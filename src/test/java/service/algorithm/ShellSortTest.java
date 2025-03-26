package service.algorithm;

import com.krs.service.factory.SortFactory;
import com.krs.service.sort.model.AlgorithmType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ShellSortTest {

    private <T extends Number> void executeSort(int parallelism, List<T> array, Comparator<T> comparator) {
        try {
            ForkJoinPool pool = new ForkJoinPool(parallelism);
            pool.invoke(SortFactory.create(Objects.requireNonNull(AlgorithmType.SHELL), comparator, array));
        } catch (IllegalArgumentException e) {
            fail("Error: Invalid parallelism level (" + parallelism + "). " + e.getMessage());
        } catch (SecurityException e) {
            fail("Error: Security manager restriction - " + e.getMessage());
        }
    }

    @Test
    public void givenSmallIntegerArray_whenShellSort_thenSortArray() {
        List<Integer> array = new ArrayList<>(List.of(5, 3, 8, 1, 2));
        List<Integer> expected = new ArrayList<>(List.of(1, 2, 3, 5, 8));
        executeSort(2, array, Integer::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenLargeIntegerArray_whenShellSort_thenSortArray() {
        List<Integer> array = new ArrayList<>(List.of(10, 7, 8, 9, 1, 5, 3, 6, 2, 4));
        List<Integer> expected = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        executeSort(4, array, Integer::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenLargerIntegerArray_whenShellSort_thenSortArray() {
        Random random = new Random();
        List<Integer> array = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            array.add(random.nextInt(1000000));
        }

        List<Integer> expected = new ArrayList<>(array);
        expected.sort(Comparator.naturalOrder());

        executeSort(4, array, Integer::compare);

        assertEquals(expected, array);
    }

    @Test
    public void givenSingleIntegerArray_whenShellSort_returnSameArray() {
        List<Integer> array = new ArrayList<>(List.of(42));
        List<Integer> expected = new ArrayList<>(List.of(42));
        executeSort(1, array, Integer::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenSortedIntegerArray_whenShellSort_returnSameArray() {
        List<Integer> array = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> expected = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        executeSort(3, array, Integer::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenReverselySortedIntegerArray_whenShellSort_returnSortedArray() {
        List<Integer> array = new ArrayList<>(List.of(9, 7, 5, 3, 1));
        List<Integer> expected = new ArrayList<>(List.of(1, 3, 5, 7, 9));
        executeSort(3, array, Integer::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenSmallDoubleArray_whenShellSort_returnSortedArray() {
        List<Double> array = new ArrayList<>(List.of(5.1, 3.3, 8.8, 1.0, 2.5));
        List<Double> expected = new ArrayList<>(List.of(1.0, 2.5, 3.3, 5.1, 8.8));
        executeSort(2, array, Double::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenLargeDoubleArray_whenShellSort_returnSortedArray() {
        List<Double> array = new ArrayList<>(List.of(10.7, 7.3, 8.9, 9.1, 1.5, 5.4, 3.6, 6.2, 2.3, 4.4));
        List<Double> expected = new ArrayList<>(List.of(1.5, 2.3, 3.6, 4.4, 5.4, 6.2, 7.3, 8.9, 9.1, 10.7));
        executeSort(4, array, Double::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenSingleDoubleArray_whenShellSort_returnSameArray() {
        List<Double> array = new ArrayList<>(List.of(42.0));
        List<Double> expected = new ArrayList<>(List.of(42.0));
        executeSort(1, array, Double::compare);
        assertEquals(expected, array);
    }


    @Test
    public void givenSortedDoubleArray_whenShellSort_returnSameArray() {
        List<Double> array = new ArrayList<>(List.of(1.2, 2.3, 3.4, 4.5, 5.6));
        List<Double> expected = new ArrayList<>(List.of(1.2, 2.3, 3.4, 4.5, 5.6));
        executeSort(3, array, Double::compare);
        assertEquals(expected, array);
    }

    @Test
    public void givenReverselySortedDoubleArray_whenShellSort_returnSortedArray() {
        List<Double> array = new ArrayList<>(List.of(9.9, 7.7, 5.5, 3.3, 1.1));
        List<Double> expected = new ArrayList<>(List.of(1.1, 3.3, 5.5, 7.7, 9.9));
        executeSort(3, array, Double::compare);
        assertEquals(expected, array);
    }
}

