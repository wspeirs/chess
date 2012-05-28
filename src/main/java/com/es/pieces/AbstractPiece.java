package com.es.pieces;

public abstract class AbstractPiece implements Piece {

    private Color color;

    public AbstractPiece(Piece.Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
