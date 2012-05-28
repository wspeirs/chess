package com.es.pieces;

import com.es.Board;

public abstract class AbstractPiece implements Piece {

    private Color color;
    private Board board;

    public AbstractPiece(Piece.Color color, Board board) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }
}
