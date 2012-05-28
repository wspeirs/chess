package com.es.pieces;

import com.es.Board;


public class Pawn extends AbstractPiece {

    public Pawn(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "p";
        } else {
            return "P";
        }
    }
}
