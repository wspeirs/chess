package com.es.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public abstract class AbstractPiece implements Piece {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPiece.class);
    private final int[][] POSITION_VALUES;

    private Color color;
    private boolean hasMoved = false;

    public AbstractPiece(Piece.Color color, int[][] positionValues) {
        this.color = color;
        this.POSITION_VALUES = positionValues;
    }

    public Color getColor() {
        return color;
    }

    public int getPositionValue(int square) {
        int col = Board.squareToCol(square);
        int row = 7 - Board.squareToRow(square);

        if(getColor().equals(Color.BLACK)) {
            col = (7 - col);
        }

        return POSITION_VALUES[row][col] + getValue();
    }

    /**
     * Checks to see if a piece can move in a certain direction or not.
     *
     * Returns true of the position was empty.
     *
     * @param positions The list of positions to possibly add the current one to.
     * @param curIndex The current index into the above array.
     * @param position The position to be checking.
     * @return True if the spot was empty, false otherwise.
     */
    public boolean addPos(Board board, int[] positions, int curIndex, int position) {
        if(position >= 0 && position < Board.MAX_SQUARE && (position & 0x08) == 0) {
//            LOG.debug("POS: {} INDEX: {}", Integer.toHexString(position), curIndex);
            final Piece p = board.getPiece(position);

            if(p == null) {
                positions[curIndex] = position;  // add to possible positions if null
                return true;
            } else if(! p.getColor().equals(getColor())) {
                positions[curIndex] = position;  // add to possible positions for capture
                return false;
            }
        }

        positions[curIndex] = Board.MAX_SQUARE;   // fill in with our sentinel value
        return false;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void pieceMoved() {
        hasMoved = true;
    }
}
