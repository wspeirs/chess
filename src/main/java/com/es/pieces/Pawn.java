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
        int[] ret = new int[4]; // can only move in 4 positions
        int curPos = 0;

        if(getColor().equals(Color.BLACK)) {
            // straight forward moves
            if(Board.squareToRow(pos) == 6 &&
               getBoard().getPiece(pos - 8) == null &&
               getBoard().getPiece(pos - 16) == null) {
                ret[curPos++] = pos - 16;
            }

            if(pos - 8 >= 0 && getBoard().getPiece(pos - 8) == null) {
                ret[curPos++] = pos - 8;
            }

            // captures
            if(pos - 7 >= 0) {
                final Piece p = getBoard().getPiece(pos - 7);

                if(p != null && p.getColor().equals(Color.WHITE)) {
                    ret[curPos++] = pos - 7;
                }
            }

            if(pos - 9 >= 0) {
                final Piece p = getBoard().getPiece(pos - 9);

                if(p != null && p.getColor().equals(Color.WHITE)) {
                    ret[curPos++] = pos - 9;
                }
            }
        } else {
            // straight forward moves
            if(Board.squareToRow(pos) == 1 &&
               getBoard().getPiece(pos + 8) == null &&
               getBoard().getPiece(pos + 16) == null) {
                ret[curPos++] = pos + 16;
            }

            if(pos + 8 < Board.MAX_SQUARE && getBoard().getPiece(pos + 8) == null) {
                ret[curPos++] = pos + 8;
            }

            // captures
            if(pos + 7 < Board.MAX_SQUARE) {
                final Piece p = getBoard().getPiece(pos + 7);

                if(p != null && p.getColor().equals(Color.BLACK)) {
                    ret[curPos++] = pos + 7;
                }
            }

            if(pos + 9 < Board.MAX_SQUARE) {
                final Piece p = getBoard().getPiece(pos + 9);

                if(p != null && p.getColor().equals(Color.BLACK)) {
                    ret[curPos++] = pos + 9;
                }
            }
        }

        Arrays.fill(ret, curPos, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
