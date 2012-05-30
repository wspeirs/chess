package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Queen extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public Queen(Color color, Board board, int currentPosition) {
        super(color, board, currentPosition);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "q";
        } else {
            return "Q";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        final int curRow = Board.squareToRow(pos);
        int[] ret = new int[27]; // can only move in 27 places
        int curPos = 0;

        LOG.debug("CUR POS: {}", pos);

        for(int i = pos + 9; addPos(ret, curPos++, i) && Board.squareToCol(i) != 0; i += 9); // move to upper-right
        for(int i = pos - 9; addPos(ret, curPos++, i) && Board.squareToCol(i) != 7; i -= 9); // move to lower-left
        for(int i = pos + 7; addPos(ret, curPos++, i) && Board.squareToCol(i) != 0; i += 7); // move to upper-left
        for(int i = pos - 7; addPos(ret, curPos++, i) && Board.squareToCol(i) != 7; i -= 7); // move to lower-right

        for(int i = pos + 8; addPos(ret, curPos++, i); i += 8); // move forward
        for(int i = pos - 8; addPos(ret, curPos++, i); i -= 8); // move backward
        for(int i = pos + 1; Board.squareToRow(i) == curRow && addPos(ret, curPos++, i); i += 1); // move right
        for(int i = pos - 1; Board.squareToRow(i) == curRow && addPos(ret, curPos++, i); i -= 1); // move left

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
