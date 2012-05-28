package com.es.pieces;

import com.es.Board;

public class Queen extends AbstractPiece {

    public Queen(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "q";
        } else {
            return "Q";
        }
    }
}
