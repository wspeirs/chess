package com.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.AbstractPiece;
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
public class Board implements Cloneable {

    public static final Logger LOG = LoggerFactory.getLogger(Board.class);

    public static final int MAX_ROW = 8;
    public static final int MAX_COL = 8;
    public static final int MAX_SQUARE = 0x78;

    private Piece[] board;

    private int[] blackPieces;

    private int[] whitePieces;

    private int blackKing;
    private int whiteKing;

    private int hashCode;

    public Board() {
        board = new Piece[MAX_SQUARE];

        blackPieces = new int[16];

        whitePieces = new int[16];

        // fill in black's pieces
        board[0x70] = new Rook(Color.BLACK);
        board[0x71] = new Knight(Color.BLACK);
        board[0x72] = new Bishop(Color.BLACK);
        board[0x73] = new Queen(Color.BLACK);
        board[0x74] = new King(Color.BLACK);
        board[0x75] = new Bishop(Color.BLACK);
        board[0x76] = new Knight(Color.BLACK);
        board[0x77] = new Rook(Color.BLACK);

        // add pawns to the board
        for(int i=0x60; i < 0x68; ++i) {
            board[i] = new Pawn(Color.BLACK);
        }

        // fill in white's pawns
        for(int i=0x10; i < 0x18; ++i) {
            board[i] = new Pawn(Color.WHITE);
        }

        board[0x00] = new Rook(Color.WHITE);
        board[0x01] = new Knight(Color.WHITE);
        board[0x02] = new Bishop(Color.WHITE);
        board[0x03] = new Queen(Color.WHITE);
        board[0x04] = new King(Color.WHITE);
        board[0x05] = new Bishop(Color.WHITE);
        board[0x06] = new Knight(Color.WHITE);
        board[0x07] = new Rook(Color.WHITE);

        // add pieces
        int w = 0;
        int b = 0;
        for(int i=0x00; i < board.length; ++i) {
            Piece p = board[i];

            if(p != null) {
                if(p.getColor().equals(Color.WHITE)) {
                    whitePieces[w++] = i;
                } else {
                    blackPieces[b++] = i;
                }
            }
        }

        Arrays.sort(whitePieces);
        Arrays.sort(blackPieces);

        // set the kings
        blackKing = 0x74;
        whiteKing = 0x04;

        // compute the hash code
        computeHashCode();
    }
    
    public Board(String layout) throws IllegalMoveException {
        if(layout.length() != 64) {
            throw new IllegalMoveException("Not enough values for the starting board");
        }
        
        for(int i=0; i < layout.length(); ++i) {
            if(i < 8) {
                board[i + 0x70] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 16) {
                board[i + 0x60] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 24) {
                board[i + 0x50] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 32) {
                board[i + 0x40] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 40) {
                board[i + 0x30] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 48) {
                board[i + 0x20] = AbstractPiece.makePiece(layout.charAt(i));
            } else if(i < 56) {
                board[i + 0x10] = AbstractPiece.makePiece(layout.charAt(i));
            } else {
                board[i] = AbstractPiece.makePiece(layout.charAt(i));
            }
        }
        
        Arrays.fill(whitePieces, Board.MAX_SQUARE);
        Arrays.fill(blackPieces, Board.MAX_SQUARE);
        
        int wIndex = 0;
        int bIndex = 0;
        
        for(int i=0; i < Board.MAX_SQUARE; ++i) {
            if(board[i].getColor().equals(Color.WHITE)) {
                whitePieces[wIndex++] = i;
                if(board[i] instanceof King) {
                    whiteKing = i;
                }
            } else {
                blackPieces[bIndex++] = i;
                if(board[i] instanceof King) {
                    blackKing = i;
                }
            }
        }
        
        Arrays.sort(whitePieces);
        Arrays.sort(blackPieces);
        
        hashCode = 0;
    }

    // copy constructor
    public Board(Board board) {
        this.board = Arrays.copyOf(board.board, board.board.length);

        this.whitePieces = Arrays.copyOf(board.whitePieces, board.whitePieces.length);
        this.whiteKing = board.whiteKing;

        this.blackPieces = Arrays.copyOf(board.blackPieces, board.blackPieces.length);
        this.blackKing = board.blackKing;

        this.hashCode = board.hashCode;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Board) {
            Board board = (Board) obj;

            for(int i=0; i < Board.MAX_SQUARE; ++i) {
                if(this.board[i] != board.board[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    private void computeHashCode() {
        this.hashCode = 0;
        for(int i=0; i < whitePieces.length; ++i) {
            this.hashCode ^= (whitePieces[i] << (16 + i/2)) | (blackPieces[i] << (i/2));
        }
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
        Arrays.fill(whitePieces, Board.MAX_SQUARE);
        whiteKing = Board.MAX_SQUARE;

        Arrays.fill(blackPieces, Board.MAX_SQUARE);
        blackKing = Board.MAX_SQUARE;

        Arrays.fill(board, null);

        // compute the new hash code
        computeHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        for(int r=7; r >= 0; --r) {
            for(int c = 0; c < 8; ++c) {
                Piece p = board[(r << 4) + c];

                sb.append(p == null ? "-" : p.toString());
                sb.append(" ");
            }
            sb.append("\n");
        }
        sb.append("\n");
        
        return sb.toString();
    }

    public void makeMove(int fromSquare, int toSquare) throws IllegalMoveException {
        makeMove(fromSquare, toSquare, true);
    }

    /**
     * Moves the piece from one square to another.
     * @param fromSquare The starting square.
     * @param toSquare The ending square.
     * @throws IllegalMoveException
     */
    public void makeMove(int fromSquare, int toSquare, boolean kingCheck) throws IllegalMoveException {
        if(fromSquare == -1) {
            this.castel(toSquare == 1 ? Color.WHITE : Color.BLACK, true);
            return;
        } else if(fromSquare == -2){
            this.castel(toSquare == 1 ? Color.WHITE : Color.BLACK, false);
            return;
        }

        Piece fromPiece = board[fromSquare];

        if(fromPiece == null) {
            throw new IllegalMoveException("There is no piece on square: 0x" + Integer.toHexString(fromSquare));
        }

        // check to see if the move is legal or not
        if(Arrays.binarySearch(fromPiece.generateAllMoves(this, fromSquare), toSquare) < 0) {
            LOG.error("Illegal move {} - > {} for {}", new String[] { Integer.toHexString(fromSquare), Integer.toHexString(toSquare), fromPiece.toString() } );
            throw new IllegalMoveException("That move is not legal for " + fromPiece.toString());
        }

        Piece toPiece = board[toSquare];

        if(toPiece != null) {
            capturePiece(toSquare);
        }

        // set the piece on the board
        board[toSquare] = fromPiece;
        board[fromSquare] = null;

        // update the piece's location
        if(fromPiece.getColor().equals(Color.WHITE)) {
            whitePieces[Arrays.binarySearch(whitePieces, fromSquare)] = toSquare;
            Arrays.sort(whitePieces);
            if(fromPiece instanceof King) {
                whiteKing = toSquare;
            }
        } else {
            blackPieces[Arrays.binarySearch(blackPieces, fromSquare)] = toSquare;
            Arrays.sort(blackPieces);
            if(fromPiece instanceof King) {
                blackKing = toSquare;
            }
        }

        if(kingCheck) {
            // make sure that this color's king is not in check
            boolean inCheck = fromPiece.getColor().equals(Color.WHITE) ? isInCheck(whiteKing) : isInCheck(blackKing);

            // need to undo the move
            if(inCheck) {
                board[fromSquare] = fromPiece;
                board[toSquare] = toPiece;

                if(toPiece != null) {
                    addPiece(toPiece, toSquare);
                }
                final String from = Integer.toHexString(fromSquare);
                final String to = Integer.toHexString(toSquare);

                throw new IllegalMoveException("The move 0x" + from + " -> 0x" + to + " would put the king into check", true);
            }
        }

        fromPiece.pieceMoved(); // mark the piece has having moved
        computeHashCode();  // re-compute the hash code

        if(LOG.isTraceEnabled()) {
            LOG.trace(this.toString());
        }
    }

    public void castel(Color color, boolean kingSide) throws IllegalMoveException {
        int[] pieces = color.equals(Color.WHITE) ? whitePieces : blackPieces;
        int kingPos = color.equals(Color.WHITE) ? whiteKing : blackKing;
        int rookPos;

        if(board[kingPos] != null && board[kingPos].hasMoved()) {
            throw new IllegalMoveException("King has already moved, cannot castle");
        }

        if(kingSide) {
            rookPos = color.equals(Color.WHITE) ? 0x07 : 0x77;
        } else {
            rookPos = color.equals(Color.WHITE) ? 0x00 : 0x70;
        }

        if(board[rookPos] != null && board[rookPos].hasMoved()) {
            throw new IllegalMoveException("Rook has already moved, cannot castle");
        }

        // make sure the spaces between are clear
        for(int i=Math.min(kingPos, rookPos) + 1; i < Math.max(kingPos, rookPos); ++i) {
            if(board[i] != null) {
                throw new IllegalMoveException("Pieces between king and rook, cannot castle");
            }
        }

        board[kingPos].pieceMoved();
        board[rookPos].pieceMoved();

        if(kingSide) {
            // move the king
            pieces[Arrays.binarySearch(pieces, kingPos)] = kingPos + 2;
            board[kingPos + 2] = board[kingPos];
            board[kingPos] = null;
            kingPos += 2;
            Arrays.sort(pieces);

            // move the rook
            pieces[Arrays.binarySearch(pieces, rookPos)] =  kingPos - 1;
            board[kingPos - 1] = board[rookPos];
            board[rookPos] = null;
            Arrays.sort(pieces);
        } else {
            // move the king
            pieces[Arrays.binarySearch(pieces, kingPos)] = kingPos - 2;
            board[kingPos - 2] = board[kingPos];
            board[kingPos] = null;
            kingPos -= 2;
            Arrays.sort(pieces);

            // move the rook
            pieces[Arrays.binarySearch(pieces, rookPos)] =  kingPos + 1;
            board[kingPos + 1] = board[rookPos];
            board[rookPos] = null;
            Arrays.sort(pieces);
        }

        computeHashCode(); // re-compute the hash code
    }

    /**
     * Given a king, checks to see if it is in check.
     * @param king The king to check.
     * @return True if the king is in check, false otherwise.
     */
    public boolean isInCheck(int kingPos) {
        final King king = (King) board[kingPos];
        final int[] pieces = king.getColor().equals(Color.WHITE) ? blackPieces : whitePieces;

        for(int p:pieces) {
            if(p != Board.MAX_SQUARE && Arrays.binarySearch(board[p].generateAllMoves(this, p), kingPos) >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a piece from the board, adding it to the captured pieces set.
     * @param piece The piece to remove from the board.
     */
    public void capturePiece(int pos) {
        Color c = board[pos].getColor();

        // remove it from the pieces on the board and add it to the captured pieces
        if(c.equals(Color.BLACK)) {
            blackPieces[Arrays.binarySearch(blackPieces, pos)] = Board.MAX_SQUARE;
            Arrays.sort(blackPieces);
        } else {
            whitePieces[Arrays.binarySearch(whitePieces, pos)] = Board.MAX_SQUARE;
            Arrays.sort(whitePieces);
        }

        board[pos] = null; // remove the piece from the board
        computeHashCode(); // re-compute the hash code

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
            blackPieces[Arrays.binarySearch(blackPieces, Board.MAX_SQUARE)] = square;
            Arrays.sort(blackPieces);

            if(piece instanceof King) {
                blackKing = square;
            }
        } else {
            whitePieces[Arrays.binarySearch(whitePieces, Board.MAX_SQUARE)] = square;
            Arrays.sort(whitePieces);

            if(piece instanceof King) {
                whiteKing = square;
            }
        }

        board[square] = piece; // add the piece to the board
        computeHashCode(); // re-compute the hash code

    }

    public int[] getPieces(Color color) {
        return color.equals(Color.WHITE) ? whitePieces : blackPieces;
    }

    public List<Integer> getPiecsOfType(Color color, Class<? extends Piece> pieceType) {
        final int[] pieces = getPieces(color);
        final ArrayList<Integer> ret = new ArrayList<Integer>();

        for(int p:pieces) {
            if(p != Board.MAX_SQUARE && board[p].getClass().equals(pieceType)) {
                ret.add(p);
            }
        }

        return ret;
    }
}
