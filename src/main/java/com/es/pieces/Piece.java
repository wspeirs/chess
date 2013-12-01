package com.es.pieces;

import com.es.Board;
import com.fluxchess.jcpi.models.GenericColor;

public interface Piece {

    public class Color {

        public static final Color BLACK = new Color("BLACK");
        public static final Color WHITE = new Color("WHITE");

        private final String name;

        private Color(String name) {
            this.name = name;
        }

        public Color inverse() {
            return this.equals(BLACK) ? WHITE : BLACK;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Color fromGenericColor(GenericColor color) {
            return color.equals(GenericColor.BLACK) ? BLACK : WHITE;
        }
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
    public int[] generateAllMoves(Board board, int curPos);

    public int getValue();

    public int getPositionValue(int square);

    public boolean hasMoved();
    public void pieceMoved();
}
