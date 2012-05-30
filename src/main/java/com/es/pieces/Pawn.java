package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;


public class Pawn extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);

    public Pawn(Color color, Board board, int currentPosition) {
        super(color, board, currentPosition);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "p";
        } else {
            return "P";
        }
    }

    public int[] generateAllMoves() {
        final int pos = getCurPos();
        final Board board = getBoard();
        int[] ret = new int[4]; // can only move in 4 positions
        int curPos = 0;

        if(getColor().equals(Color.BLACK)) {
            // straight forward moves
            if((pos >> 4) == 6 && board.getPiece(pos - 0x10) == null && board.getPiece(pos - 0x20) == null) {
                ret[curPos++] = pos - 0x20;
            }

            if(pos - 0x10 >= 0 && board.getPiece(pos - 0x10) == null) {
                ret[curPos++] = pos - 0x10;
            }

            // capture lower-right
            Piece p = board.getPiece(pos - 0x0f);
            if(p != null && p.getColor().equals(Color.WHITE)) {
                ret[curPos++] = pos - 0x0f;
            }
            
            // capture lower-left
            p = board.getPiece(pos - 0x11); 
            if(p != null && p.getColor().equals(Color.WHITE)) {
                ret[curPos++] = pos - 0x11;
            }
        } else {
            // straight forward moves
            if((pos >> 4) == 1 && board.getPiece(pos + 0x10) == null && board.getPiece(pos + 0x20) == null) {
                ret[curPos++] = pos + 0x20;
            }

            if(pos + 0x10 < Board.MAX_SQUARE && board.getPiece(pos + 0x10) == null) {
                ret[curPos++] = pos + 0x10;
            }

            // capture upper-left
            Piece p = board.getPiece(pos + 0x0f); 
            if(p != null && p.getColor().equals(Color.BLACK)) {
                ret[curPos++] = pos + 0x0f;
            }
            
            // capture upper-right
            p = board.getPiece(pos + 0x011);
            if(p != null && p.getColor().equals(Color.BLACK)) {
                ret[curPos++] = pos + 0x11;
            }
        }

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
