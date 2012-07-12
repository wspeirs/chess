package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;


public class Pawn extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);
    private static final int[][] POSITION_VALUES =
    { {  1,  1,  1,  1,  1,  1,  1,  1 },
      { 50, 50, 50, 50, 50, 50, 50, 50 },
      { 10, 10, 20, 30, 30, 20, 10, 10 },
      {  5,  5, 10, 25, 25, 10,  5,  5 },
      {  1,  1,  1, 20, 20,  1,  1,  1 },
      {  5, -5,-10,  1,  1,-10, -5,  5 },
      {  5, 10, 10,-20,-20, 10, 10,  5 },
      {  1,  1,  1,  1,  1,  1,  1,  1 }
    };

    public Pawn(Color color) {
        super(color, POSITION_VALUES);
    }

    public int getValue() {
        return 100;
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "p";
        } else {
            return "P";
        }
    }

    public int[] generateAllMoves(Board board, int curPos) {
        int[] ret = new int[4]; // can only move in 4 positions
        int retIndex = 0;
        final int enPassant = board.getEnPassant();

        if(getColor().equals(Color.BLACK)) {
            // straight forward moves
            if((curPos >> 4) == 6 && board.getPiece(curPos - 0x10) == null && board.getPiece(curPos - 0x20) == null) {
                ret[retIndex++] = curPos - 0x20;
            }

            if(curPos - 0x10 >= 0 && board.getPiece(curPos - 0x10) == null) {
                ret[retIndex++] = curPos - 0x10;
            }

            // capture lower-right
            int move = curPos - 0x0f;
            if(move >= 0) {
                Piece p = board.getPiece(move);
                if(p != null && p.getColor().equals(Color.WHITE)) {
                    ret[retIndex++] = move;
                }
            }

            // capture lower-left
            move = curPos - 0x11;
            if(move >= 0) {
                Piece p = board.getPiece(move);
                if(p != null && p.getColor().equals(Color.WHITE)) {
                    ret[retIndex++] = move;
                }
            }
            
            // en passant
            if(enPassant != Board.MAX_SQUARE && (enPassant & 0xF0) != 0x20 && (curPos - 0x11 == enPassant || curPos - 0x0F == enPassant)) {
                ret[retIndex++] = enPassant;
            }
        } else {
            // straight forward moves
            if((curPos >> 4) == 1 && board.getPiece(curPos + 0x10) == null && board.getPiece(curPos + 0x20) == null) {
                ret[retIndex++] = curPos + 0x20;
            }

            if(curPos + 0x10 < Board.MAX_SQUARE && board.getPiece(curPos + 0x10) == null) {
                ret[retIndex++] = curPos + 0x10;
            }

            // capture upper-left
            int move = curPos + 0x0f;
            if(move < Board.MAX_SQUARE) {
                Piece p = board.getPiece(move);
                if(p != null && p.getColor().equals(Color.BLACK)) {
                    ret[retIndex++] = move;
                }
            }

            // capture upper-right
            move = curPos + 0x11;
            if(move < Board.MAX_SQUARE) {
                Piece p = board.getPiece(move);
                if(p != null && p.getColor().equals(Color.BLACK)) {
                    ret[retIndex++] = move;
                }
            }

            // en passant
            if(enPassant != Board.MAX_SQUARE && (enPassant & 0xF0) != 0x60 && (curPos + 0x11 == enPassant || curPos + 0x0F == enPassant)) {
                ret[retIndex++] = enPassant;
            }
        }

        Arrays.fill(ret, retIndex, ret.length, Board.MAX_SQUARE);   // fill the rest with MAX_SQUARE
        Arrays.sort(ret);   // sort the array

        return ret;
    }
}
