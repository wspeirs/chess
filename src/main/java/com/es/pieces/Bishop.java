package com.es.pieces;

import java.util.Arrays;

import com.es.Board;

public class Bishop extends AbstractPiece {

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

        for(int i = pos; addPos(ret, curPos++, i); i += 9); // move to upper-right
        for(int i = pos; addPos(ret, curPos++, i); i -= 9); // move to lower-left
        for(int i = pos; addPos(ret, curPos++, i); i += 7); // move to upper-left
        for(int i = pos; addPos(ret, curPos++, i); i -= 7); // move to lower-right

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
