package com.es.pieces;

import java.util.Arrays;

import com.es.Board;

public class King extends AbstractPiece {

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

        addPos(ret, curPos++, pos+9); // check upper-right
        addPos(ret, curPos++, pos-9); // check lower-left
        addPos(ret, curPos++, pos+7); // move to upper-left
        addPos(ret, curPos++, pos-7); // move to lower-right

        addPos(ret, curPos++, pos+8); // move to forward
        addPos(ret, curPos++, pos-8); // move to back
        addPos(ret, curPos++, pos+1); // move to right
        addPos(ret, curPos++, pos-1); // move to left

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
//        Arrays.sort(ret);   // sort the array

        return ret;
    }

}
