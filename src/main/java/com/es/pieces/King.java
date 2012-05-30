package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class King extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public King(Color color, Board board, int currentPosition) {
        super(color, board, currentPosition);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "k";
        } else {
            return "K";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        int[] ret = new int[8]; // can only ever move 8 positions
        int curPos = 0;

        addPos(ret, curPos++, pos + 0x11); // check upper-right
        addPos(ret, curPos++, pos - 0x11); // check lower-left
        addPos(ret, curPos++, pos + 0x0f); // move to upper-left
        addPos(ret, curPos++, pos - 0x0f); // move to lower-right

        addPos(ret, curPos++, pos + 0x10); // move to forward
        addPos(ret, curPos++, pos - 0x10); // move to back
        addPos(ret, curPos++, pos + 0x01); // move to right
        addPos(ret, curPos++, pos - 0x01); // move to left

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }

}
