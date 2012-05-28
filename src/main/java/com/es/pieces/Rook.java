package com.es.pieces;

import com.es.Board;

public class Rook extends AbstractPiece {

    public Rook(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "r";
        } else {
            return "R";
        }
    }
}
