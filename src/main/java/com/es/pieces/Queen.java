package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Queen extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);
    private static final int[][] POSITION_VALUES =
    { { -20,-10,-10, -5, -5,-10,-10,-20 },
      { -10,  1,  1,  1,  1,  1,  1,-10 },
      { -10,  1,  5,  5,  5,  5,  1,-10 },
      {  -5,  1,  5,  5,  5,  5,  1, -5 },
      {   1,  1,  5,  5,  5,  5,  1, -5 },
      { -10,  5,  5,  5,  5,  5,  1,-10 },
      { -10,  1,  5,  1,  1,  1,  1,-10 },
      { -20,-10,-10, -5, -5,-10,-10,-20 }
    };

    public Queen(Color color) {
        super(color, POSITION_VALUES);
    }

    public int getValue() {
        return 900;
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "q";
        } else {
            return "Q";
        }
    }

    public int[] generateAllMoves(Board board, int curPos) {
        int[] ret = new int[35]; // can only move in 27 places
        int retIndex = 0;

//        LOG.debug("CUR POS: {}", pos);

        for(int i = curPos + 0x11; addPos(board, ret, retIndex++, i); i += 0x11); // move to upper-right
        for(int i = curPos - 0x11; addPos(board, ret, retIndex++, i); i -= 0x11); // move to lower-left
        for(int i = curPos + 0x0f; addPos(board, ret, retIndex++, i); i += 0x0f); // move to upper-left
        for(int i = curPos - 0x0f; addPos(board, ret, retIndex++, i); i -= 0x0f); // move to lower-right

        for(int i = curPos + 0x10; addPos(board, ret, retIndex++, i); i += 0x10); // move forward
        for(int i = curPos - 0x10; addPos(board, ret, retIndex++, i); i -= 0x10); // move backward
        for(int i = curPos + 0x01; addPos(board, ret, retIndex++, i); i += 0x01); // move right
        for(int i = curPos - 0x01; addPos(board, ret, retIndex++, i); i -= 0x01); // move left

        Arrays.fill(ret, retIndex, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
