package com.es.pieces;

public interface Piece {

    public enum Color {
        BLACK,
        WHITE
    }

    /**
     * Returns the color of the piece.
     * @return The color of the piece.
     */
    public Color getColor();

    /**
     * Returns a sorted array of possible moves for the piece.
     * 
     * There are NO checks for things like putting the king into check etc.
     * 
     * @return a sorted array of possible squares the piece could move into.
     */
    public int[] generateAllMoves();
    
    public int getCurPos();
    
    public void setCurPos(int position);
}
