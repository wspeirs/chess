package com.es;

import java.util.Arrays;

public class ArraySet {

    /**
     * Given an array of <b>sorted</b> numbers, locates a value and removes it keeping the list sorted.
     * @param numbers An array of sorted numbers.
     * @param number The number to remove from the array.
     * @param sentinalNumber The sentinalNumber to tack on the end of the array.
     * @return True if the number was found and removed, false otherwise.
     */
    public static boolean removeNumber(int[] numbers, int number, int sentinalNumber) {
        final int loc = Arrays.binarySearch(numbers, number);

        if(loc < 0 || number == sentinalNumber) {
            return false;
        }

        for(int i = loc; i < numbers.length - 1; ++i) {
            numbers[i] = numbers[i+1];
        }

        numbers[numbers.length-1] = sentinalNumber;

        return true;
    }

    public static boolean addNumber(int[] numbers, int number) {
        final int loc = (Arrays.binarySearch(numbers, number) + 1) * -1;

        // make sure the number isn't already in there
        if(loc < 0) {
            return false;
        }

        // shift everything down to make room
        for(int i=numbers.length-1; i > loc; --i) {
            numbers[i] = numbers[i-1];
        }

        // insert the number
        numbers[loc] = number;

        return true;
    }

    public static int[] unionSets(int[] set1, int[] set2) {
        int[] ret = new int[Math.max(set1.length, set2.length)];
        int i = 0, j = 0, k = 0;

        while(i < set1.length && j < set2.length) {
            // in both sets
            if(set1[i] == set2[j]) {
                ret[k++] = set1[i];
                ++i;
                ++j;
            } else if(set1[i] < set2[j]) {
                ++i;
            } else {
                ++j;
            }
        }

        return k == ret.length ? ret : Arrays.copyOf(ret, k);
    }
}
