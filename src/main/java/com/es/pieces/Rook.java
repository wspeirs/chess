package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Rook extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);
    private static final int[][] POSITION_VALUES =
    { {  1,  1,  1,  1,  1,  1,  1,  1 },
      {  5, 10, 10, 10, 10, 10, 10,  5 },
      { -5,  1,  1,  1,  1,  1,  1, -5 },
      { -5,  1,  1,  1,  1,  1,  1, -5 },
      { -5,  1,  1,  1,  1,  1,  1, -5 },
      { -5,  1,  1,  1,  1,  1,  1, -5 },
      { -5,  1,  1,  1,  1,  1,  1, -5 },
      {  1,  1,  1,  5,  5,  1,  1,  1 }
    };

    public Rook(Color color) {
        super(color, POSITION_VALUES);
    }

    public int getValue() {
        return 500;
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "r";
        } else {
            return "R";
        }
    }

    public int[] generateAllMoves(Board board, int curPos) {
        int[] ret = new int[18]; // can only move in 14 places
        int retIndex = 0;

        for(int i = curPos + 0x10; addPos(board, ret, retIndex++, i); i += 0x10); // move forward
        for(int i = curPos - 0x10; addPos(board, ret, retIndex++, i); i -= 0x10); // move backward
        for(int i = curPos + 0x01; addPos(board, ret, retIndex++, i); i += 0x01); // move right
        for(int i = curPos - 0x01; addPos(board, ret, retIndex++, i); i -= 0x01); // move left

        Arrays.fill(ret, retIndex, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
