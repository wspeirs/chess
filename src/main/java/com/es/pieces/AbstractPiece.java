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
    
    public static Piece makePiece(char p) {
        switch(p) {
        case 'P': return new Pawn(Color.WHITE);
        case 'p': return new Pawn(Color.BLACK);
        case 'R': return new Rook(Color.WHITE);
        case 'r': return new Rook(Color.BLACK);
        case 'N': return new Knight(Color.WHITE);
        case 'n': return new Knight(Color.BLACK);
        case 'B': return new Bishop(Color.WHITE);
        case 'b': return new Bishop(Color.BLACK);
        case 'Q': return new Queen(Color.WHITE);
        case 'q': return new Queen(Color.BLACK);
        case 'K': return new King(Color.WHITE);
        case 'k': return new King(Color.BLACK);
        default: return null;
        }
    }

    public Color getColor() {
        return color;
    }

    public int getPositionValue(int square) {
        int col = Board.squareToCol(square);
        int row = Board.squareToRow(square);

        if(getColor().equals(Color.BLACK)) {
            col = (7 - col);
        } else {
            row = 7 - row;
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
