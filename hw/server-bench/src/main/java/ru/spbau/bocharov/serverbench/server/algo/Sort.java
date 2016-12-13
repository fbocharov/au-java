package ru.spbau.bocharov.serverbench.server.algo;

public class Sort {

    public static void insertionSort(int[] array) {
        for (int i = 0; i < array.length; ++i) {
            for (int j = i; j > 0; --j) {
                if (array[j] < array[j - 1]) {
                    swap(array, j, j - 1);
                } else {
                    break;
                }
            }
        }
    }

    private static void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
