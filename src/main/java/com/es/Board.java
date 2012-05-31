package com.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Bishop;
import com.es.pieces.King;
import com.es.pieces.Knight;
import com.es.pieces.Pawn;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;
import com.es.pieces.Queen;
import com.es.pieces.Rook;

/**
 * A representation of a chess board.
 *
 * The layout is as follows where upper case is white and lower is black
 * <code>
 * 7 r n b q k b n r
 * 6 p p p p p p p p
 * 5 - - - - - - - -
 * 4 - - - - - - - -
 * 3 - - - - - - - -
 * 2 - - - - - - - -
 * 1 P P P P P P P P
 * 0 R N B Q K B N R
 *   0 1 2 3 4 5 6 7
 * </code>
 *
 * The queen's rook for white is [0][0], then [0][1] is the knight.
 * The squares are labeled:
 * <code>
 * 70 71 72 73 74 75 76 77 | 78 79 7a 7b 7c 7d 7e 7f
 * 60 61 62 63 64 65 66 67 | 68 69 6a 6b 6c 6d 6e 6f
 * 50 51 52 53 54 55 56 57 | 58 59 5a 5b 5c 5d 5e 5f
 * 40 41 42 43 44 45 46 47 | 48 49 4a 4b 4c 4d 4e 4f
 * 30 31 32 33 34 35 36 37 | 38 39 3a 3b 3c 3d 3e 3f
 * 20 21 22 23 24 25 26 27 | 28 29 2a 2b 2c 2d 2e 2f
 * 10 11 12 13 14 15 16 17 | 18 19 1a 1b 1c 1d 1e 1f
 * 00 01 02 03 04 05 06 07 | 08 09 0a 0b 0c 0d e0 0f
 * </code>
 *
 */
public class Board {

    public static final Logger LOG = LoggerFactory.getLogger(Board.class);

    public static final int MAX_ROW = 8;
    public static final int MAX_COL = 8;
    public static final int MAX_SQUARE = 0x80;

    private Piece[] board = new Piece[MAX_SQUARE-1];

    private Set<Piece> blackPieces = new HashSet<Piece>();
    private Set<Piece> blackCapturedPieces = new HashSet<Piece>();

    private Set<Piece> whitePieces = new HashSet<Piece>();
    private Set<Piece> whiteCapturedPieces = new HashSet<Piece>();

    private King blackKing;
    private King whiteKing;

    public Board() {
        // fill in black's pieces
        board[0x70] = new Rook(Color.BLACK,   this, 0x70);
        board[0x71] = new Knight(Color.BLACK, this, 0x71);
        board[0x72] = new Bishop(Color.BLACK, this, 0x72);
        board[0x73] = new Queen(Color.BLACK,  this, 0x73);
        board[0x74] = new King(Color.BLACK,   this, 0x74);
        board[0x75] = new Bishop(Color.BLACK, this, 0x75);
        board[0x76] = new Knight(Color.BLACK, this, 0x76);
        board[0x77] = new Rook(Color.BLACK,   this, 0x77);

        // add black's pieces to the set
        for(int i=0x70; i < 0x78; ++i) {
            blackPieces.add(board[i]);
        }

        // add pawns to the board and set of pieces
        for(int i=0x60; i < 0x68; ++i) {
            board[i] = new Pawn(Color.BLACK, this, i);
            blackPieces.add(board[i]);
        }

        blackKing = (King) board[0x74];

        // fill in white's pieces
        for(int i=0x10; i < 0x18; ++i) {
            board[i] = new Pawn(Color.WHITE, this, i);
            whitePieces.add(board[i]);
        }

        board[0x00] = new Rook(Color.WHITE,   this, 0x00);
        board[0x01] = new Knight(Color.WHITE, this, 0x01);
        board[0x02] = new Bishop(Color.WHITE, this, 0x02);
        board[0x03] = new Queen(Color.WHITE,  this, 0x03);
        board[0x04] = new King(Color.WHITE,   this, 0x04);
        board[0x05] = new Bishop(Color.WHITE, this, 0x05);
        board[0x06] = new Knight(Color.WHITE, this, 0x06);
        board[0x07] = new Rook(Color.WHITE,   this, 0x07);

        // add white's pieces to the set
        for(int i=0x00; i < 0x08; ++i) {
            whitePieces.add(board[i]);
        }

        whiteKing = (King) board[0x04];
    }
    
    public static int squareToRow(int square) {
        return square >> 4;
    }

    public static int squareToCol(int square) {
        return square & 0x07;
    }

    public static int rowColToSquare(int row, int col) {
        return (row << 4) + col;
    }

    public Piece getPiece(int square) {
        return this.board[square];
    }
    
    public Piece[] getBoard() {
        return board;
    }
    
    /**
     * Removes all the pieces from the board.
     * Useful for debugging.
     */
    public void clearBoard() {
        whitePieces.clear();
        whiteCapturedPieces.clear();
        whiteKing = null;
        
        blackPieces.clear();
        blackCapturedPieces.clear();
        blackKing = null;
        
        Arrays.fill(board, null);
    }

    public void printBoard() {
        for(int r=7; r >= 0; --r) {
            for(int c = 0; c < 8; ++c) {
                Piece p = board[(r << 4) + c];

                System.out.print(p == null ? "-" : p.toString());
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Moves the piece from one square to another.
     * @param fromSquare The starting square.
     * @param toSquare The ending square.
     * @throws IllegalMoveException
     */
    public void makeMove(int fromSquare, int toSquare) throws IllegalMoveException {
        Piece fromPiece = board[fromSquare];

        if(fromPiece == null) {
            throw new IllegalMoveException("There is no piece on square: " + fromSquare);
        }

        // check to see if the move is legal or not
        if(Arrays.binarySearch(fromPiece.generateAllMoves(), toSquare) < 0) {
            LOG.error("Illegal move {} - > {} for {}", new String[] { Integer.toHexString(fromSquare), Integer.toHexString(toSquare), fromPiece.toString() } );
            throw new IllegalMoveException("That move is not legal for " + fromPiece.toString());
        }

        Piece toPiece = board[toSquare];

        if(toPiece != null) {
            capturePiece(toPiece);
        }

        // set the piece on the board
        board[toSquare] = fromPiece;
        board[fromSquare] = null;

        // update the piece's position
        fromPiece.setCurPos(toSquare);

        // make sure that this color's king is not in check
        boolean inCheck = fromPiece.getColor().equals(Color.WHITE) ? isInCheck(whiteKing) : isInCheck(blackKing);

        // need to undo the move
        if(inCheck) {
            board[fromSquare] = fromPiece;
            board[toSquare] = toPiece;

            if(toPiece != null) {
                addPiece(toPiece, toSquare);
            }
            throw new IllegalMoveException("That move would put the king into check");
        }

        if(LOG.isDebugEnabled()) {
            this.printBoard();
        }
    }
    
    /**
     * Given a king, checks to see if it is in check.
     * @param king The king to check.
     * @return True if the king is in check, false otherwise.
     */
    private boolean isInCheck(King king) {
        final int kingPos = king.getCurPos();
        final Set<Piece> pieces = king.getColor().equals(Color.WHITE) ? blackPieces : whitePieces;

        for(Piece p:pieces) {
            if(Arrays.binarySearch(p.generateAllMoves(), kingPos) >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a piece from the board, adding it to the captured pieces set.
     * @param piece The piece to remove from the board.
     */
    public void capturePiece(Piece piece) {
        Color c = piece.getColor();

        // remove it from the pieces on the board and add it to the captured pieces
        if(c.equals(Color.BLACK)) {
            blackPieces.remove(piece);
            blackCapturedPieces.add(piece);
        } else {
            whitePieces.remove(piece);
            whiteCapturedPieces.add(piece);
        }

        // remove the piece from the board
        board[piece.getCurPos()] = null;
        piece.setCurPos(MAX_SQUARE);
    }

    /**
     * Adds a piece to the board removing it from the captured pieces if captured.
     * @param piece The piece to add to the board.
     * @param square The square to add the piece to.
     */
    public void addPiece(Piece piece, int square) {
        Color c = piece.getColor();

        // remove it from the captured pieces and add it to the pieces on the board
        if(c.equals(Color.BLACK)) {
            blackPieces.add(piece);
            blackCapturedPieces.remove(piece);
        } else {
            whitePieces.add(piece);
            whiteCapturedPieces.remove(piece);
        }

        // add the piece to the board
        board[square] = piece;
        piece.setCurPos(square);
    }
    
    public Set<Piece> getPieces(Color color) {
        return color.equals(Color.WHITE) ? whitePieces : blackPieces;
    }
    
    public List<Piece> getPiecsOfType(Color color, Class<? extends Piece> pieceType) {
        final Set<Piece> pieces = getPieces(color);
        final ArrayList<Piece> ret = new ArrayList<Piece>();

        for(Piece p:pieces) {
            if(p.getClass().equals(pieceType)) {
                ret.add(p);
            }
        }

        return ret;
    }
}
