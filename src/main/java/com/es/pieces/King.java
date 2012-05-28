package com.es.pieces;

import com.es.Board;

public class King extends AbstractPiece {

    public King(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "k";
        } else {
            return "K";
        }
    }
}
