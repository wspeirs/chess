package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Bishop extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public Bishop(Color color, Board board, int curPos) {
        super(color, board, curPos);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "b";
        } else {
            return "B";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        int[] ret = new int[17]; // can only ever move in 13 positions
        int curPos = 0;

        LOG.debug("CUR POS: {}", pos);

        for(int i = pos + 0x11; addPos(ret, curPos++, i); i += 0x11); // move to upper-right
        for(int i = pos - 0x11; addPos(ret, curPos++, i); i -= 0x11); // move to lower-left
        for(int i = pos + 0x0f; addPos(ret, curPos++, i); i += 0x0f); // move to upper-left
        for(int i = pos - 0x0f; addPos(ret, curPos++, i); i -= 0x0f); // move to lower-right

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
