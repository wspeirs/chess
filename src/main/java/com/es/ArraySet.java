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
    
    public static void addNumber(int[] numbers, int number) {
        final int loc = (Arrays.binarySearch(numbers, number) + 1) * -1;
        
        // make sure the number isn't already in there
        if(loc < 0) {
            return;
        }
        
        for(int i=numbers.length-1; i > loc; --i) {
            numbers[i] = numbers[i-1];
        }
        
        numbers[loc] = number;
    }
}
