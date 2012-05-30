package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Knight extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public Knight(Color color, Board board, int currentPosition) {
        super(color, board, currentPosition);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "n";
        } else {
            return "N";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        int[] ret = new int[8]; // can only ever move in 8 positions
        int curPos = 0;

        addPos(ret, curPos++, pos+17); // check up 2 right 1
        addPos(ret, curPos++, pos+15); // check up 2 left 1
        addPos(ret, curPos++, pos+10); // check up 1 right 2
        addPos(ret, curPos++, pos+6); // check up 1 left 2

        addPos(ret, curPos++, pos-17); // check down 2 left 1
        addPos(ret, curPos++, pos-15); // check down 2 right 1
        addPos(ret, curPos++, pos-10); // check down 1 left 2
        addPos(ret, curPos++, pos-6); // check down 1 right 2

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
