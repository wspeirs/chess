package com.es.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public abstract class AbstractPiece implements Piece {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPiece.class);

    private Color color;
    private Board board;
    private int curPos;

    public AbstractPiece(Piece.Color color, Board board, int currentPosition) {
        this.color = color;
        this.board = board;
        this.curPos = currentPosition;
    }

    public Color getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurPos() {
        return curPos;
    }

    public void setCurPos(int curPos) {
        this.curPos = curPos;
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
    public boolean addPos(int[] positions, int curIndex, int position) {
        if(position >= 0 && position < Board.MAX_SQUARE && (position & 0x08) == 0) {
            LOG.debug("POS: {} INDEX: {}", Integer.toHexString(position), curIndex);
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
}
