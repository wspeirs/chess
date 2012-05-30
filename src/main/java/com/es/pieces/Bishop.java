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
        int[] ret = new int[13]; // can only ever move in 13 positions
        int curPos = 0;

        LOG.debug("CUR POS: {}", pos);

        for(int i = pos + 9; addPos(ret, curPos++, i) && Board.squareToCol(i) != 0; i += 9); // move to upper-right
        for(int i = pos - 9; addPos(ret, curPos++, i) && Board.squareToCol(i) != 7; i -= 9); // move to lower-left
        for(int i = pos + 7; addPos(ret, curPos++, i) && Board.squareToCol(i) != 0; i += 7); // move to upper-left
        for(int i = pos - 7; addPos(ret, curPos++, i) && Board.squareToCol(i) != 7; i -= 7); // move to lower-right

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
