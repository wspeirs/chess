package com.es;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ArrayUtilsTest {

    private static int SENTINAL = 100;

    @Test
    public void testRemoveNumber() {
        int[] nums = { 0, 2, 4, 6, 8 };
        int[] ret  = { 0, 4, 6, 8, SENTINAL };

        ArraySet.removeNumber(nums, 2, SENTINAL);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testRemoveFirstNumber() {
        int[] nums = { 0, 2, 4, 6, 8 };
        int[] ret  = { 2, 4, 6, 8, SENTINAL };

        ArraySet.removeNumber(nums, 0, SENTINAL);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testRemoveLastNumber() {
        int[] nums = { 0, 2, 4, 6, 8 };
        int[] ret  = { 0, 2, 4, 6, SENTINAL };

        ArraySet.removeNumber(nums, 8, SENTINAL);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testAddNumber() {
        int[] nums = { 0, 2, 4, 6, 8, SENTINAL };
        int[] ret  = { 0, 2, 3, 4, 6, 8 };

        ArraySet.addNumber(nums, 3);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testAddFirstNumber() {
        int[] nums = { 0, 2, 4, 6, 8, SENTINAL };
        int[] ret  = { -1, 0, 2, 4, 6, 8 };

        ArraySet.addNumber(nums, -1);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testAddLastNumber() {
        int[] nums = { 0, 2, 4, 6, 8, SENTINAL };
        int[] ret  = { 0, 2, 4, 6, 8, 9 };

        ArraySet.addNumber(nums, 9);

        assertEquals(ret.length, nums.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], nums[i]);
        }
    }

    @Test
    public void testUnionSameSets() {
        int[] set1 = { 0, 2, 4, 6, 8 };
        int[] set2 = { 0, 2, 4, 6, 8 };
        int[] ret  = { 0, 2, 4, 6, 8 };

        int[] res = ArraySet.unionSets(set1, set2);

        assertEquals(ret.length, res.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], res[i]);
        }
    }

    @Test
    public void testUnion1LongerSets() {
        int[] set1 = { 0, 2, 3, 4, 6, 8 };
        int[] set2 = { 0, 2, 4, 6, 8 };
        int[] ret  = { 0, 2, 4, 6, 8 };

        int[] res = ArraySet.unionSets(set1, set2);

        assertEquals(ret.length, res.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], res[i]);
        }
    }

    @Test
    public void testUnionBlankSets() {
        int[] set1 = {  };
        int[] set2 = {  };
        int[] ret  = {  };

        int[] res = ArraySet.unionSets(set1, set2);

        assertEquals(ret.length, res.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], res[i]);
        }
    }

    @Test
    public void testUnionDisjointSets() {
        int[] set1 = { 0, 2, 4, 6, 8 };
        int[] set2 = { 1, 3, 5, 7, 9 };
        int[] ret  = {  };

        int[] res = ArraySet.unionSets(set1, set2);

        assertEquals(ret.length, res.length);

        for(int i=0; i < ret.length; ++i) {
            assertEquals(ret[i], res[i]);
        }
    }

}
