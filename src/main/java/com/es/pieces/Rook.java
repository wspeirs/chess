package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class Rook extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public Rook(Color color, Board board, int currentPosition) {
        super(color, board, currentPosition);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "r";
        } else {
            return "R";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        final int curRow = Board.squareToRow(pos);
        int[] ret = new int[14]; // can only move in 14 places
        int curPos = 0;

        for(int i = pos + 8; addPos(ret, curPos++, i); i += 8); // move forward
        for(int i = pos - 8; addPos(ret, curPos++, i); i -= 8); // move backward
        for(int i = pos + 1; Board.squareToRow(i) == curRow && addPos(ret, curPos++, i); i += 1); // move right
        for(int i = pos - 1; Board.squareToRow(i) == curRow && addPos(ret, curPos++, i); i -= 1); // move left

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
