package com.es.pieces;

import com.es.Board;

public class Bishop extends AbstractPiece {

    public Bishop(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "b";
        } else {
            return "B";
        }
    }
}
