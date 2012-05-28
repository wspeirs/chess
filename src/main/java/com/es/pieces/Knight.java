package com.es.pieces;

import com.es.Board;

public class Knight extends AbstractPiece {

    public Knight(Color color, Board board) {
        super(color, board);
    }

    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "n";
        } else {
            return "N";
        }
    }
}
