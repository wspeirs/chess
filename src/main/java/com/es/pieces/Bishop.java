package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Bishop extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);
    private static final int[][] POSITION_VALUES =
    { { -20,-10,-10,-10,-10,-10,-10,-20 },
      { -10,  1,  1,  1,  1,  1,  1,-10 },
      { -10,  1,  5, 10, 10,  5,  1,-10 },
      { -10,  5,  5, 10, 10,  5,  5,-10 },
      { -10,  1, 10, 10, 10, 10,  1,-10 },
      { -10, 10, 10, 10, 10, 10, 10,-10 },
      { -10,  5,  1,  1,  1,  1,  5,-10 },
      { -20,-10,-10,-10,-10,-10,-10,-20 }
    };

    public Bishop(Color color) {
        super(color, POSITION_VALUES);
    }

    public int getValue() {
        return 325;
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "b";
        } else {
            return "B";
        }
    }

    public int[] generateAllMoves(Board board, int curPos) {
        int[] ret = new int[17]; // can only ever move in 13 positions
        int retIndex = 0;

//        LOG.debug("CUR POS: {}", pos);

        for(int i = curPos + 0x11; addPos(board, ret, retIndex++, i); i += 0x11); // move to upper-right
        for(int i = curPos - 0x11; addPos(board, ret, retIndex++, i); i -= 0x11); // move to lower-left
        for(int i = curPos + 0x0f; addPos(board, ret, retIndex++, i); i += 0x0f); // move to upper-left
        for(int i = curPos - 0x0f; addPos(board, ret, retIndex++, i); i -= 0x0f); // move to lower-right

        Arrays.fill(ret, retIndex, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
