package com.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jcpi.data.GenericBoard;
import jcpi.data.GenericCastling;
import jcpi.data.GenericColor;
import jcpi.data.GenericFile;
import jcpi.data.GenericPiece;
import jcpi.data.GenericPosition;
import jcpi.data.GenericRank;

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
public final class Board implements Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(Board.class);

    private static final String LINE_BREAK = System.getProperty("line.separator");

    public static final int MAX_ROW = 8;
    public static final int MAX_COL = 8;
    public static final int MAX_SQUARE = 0x78;


    private final Piece[] board;

    private final int[] blackPieces;
    private final int[] whitePieces;

    // TODO: CHANGE BACK TO PRIVATE
    public int blackKing;
    public int whiteKing;

    private boolean whiteKingCastle = false;
    private boolean whiteQueenCastle = false;
    private boolean blackKingCastle = false;
    private boolean blackQueenCastle = false;

    private int enPassant = Board.MAX_SQUARE;

	private Color activeColor = Color.WHITE;

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

        // set the castles
        whiteKingCastle = whiteQueenCastle = blackKingCastle = blackQueenCastle = true;

        // set the en passe
        enPassant = Board.MAX_SQUARE;

        // compute the hash code
        computeHashCode();
    }

    // copy constructor
    public Board(Board board) {
        this.board = Arrays.copyOf(board.board, board.board.length);

        this.whitePieces = Arrays.copyOf(board.whitePieces, board.whitePieces.length);
        this.whiteKing = board.whiteKing;

        this.blackPieces = Arrays.copyOf(board.blackPieces, board.blackPieces.length);
        this.blackKing = board.blackKing;

        this.hashCode = board.hashCode;

        this.whiteKingCastle = board.whiteKingCastle;
        this.whiteQueenCastle = board.whiteQueenCastle;
        this.blackKingCastle = board.blackKingCastle;
        this.blackQueenCastle = board.blackQueenCastle;

        this.enPassant = board.enPassant;
    }

	// construct from a GenericBoard
    public Board(GenericBoard genericBoard) {
        board = new Piece[MAX_SQUARE];

        blackPieces = new int[16];
        whitePieces = new int[16];
        int w = 0;
        int b = 0;

        Arrays.fill(whitePieces, Board.MAX_SQUARE);
        Arrays.fill(blackPieces, Board.MAX_SQUARE);

		// Initialize the board
        for (GenericPosition position : GenericPosition.values()) {
			GenericPiece genericPiece = genericBoard.getPiece(position);
			if (genericPiece != null) {
	            int file = Arrays.asList(GenericFile.values()).indexOf(position.file);
	            int rank = Arrays.asList(GenericRank.values()).indexOf(position.rank);

	            int intPosition = rank * 16 + file;

	            board[intPosition] = AbstractPiece.makePiece(genericPiece.toChar());

	            if (genericPiece.color == GenericColor.WHITE) {
                    whitePieces[w++] = intPosition;
                } else {
                    blackPieces[b++] = intPosition;
	            }

	            if (genericPiece == GenericPiece.BLACKKING) {
	            	blackKing = intPosition;
	            } else if (genericPiece == GenericPiece.WHITEKING) {
	            	whiteKing = intPosition;
	            }
			}
        }

        Arrays.sort(whitePieces);
        Arrays.sort(blackPieces);

        if (genericBoard.getActiveColor().equals(GenericColor.WHITE)) {
        	activeColor = Color.WHITE;
        } else {
        	assert genericBoard.getActiveColor().equals(GenericColor.BLACK);
        	activeColor = Color.BLACK;
        }

        // set the castling
        if(genericBoard.getCastling(GenericColor.WHITE, GenericCastling.KINGSIDE) != null) {
            whiteKingCastle = true;
        }

        if(genericBoard.getCastling(GenericColor.WHITE, GenericCastling.QUEENSIDE) != null) {
            whiteQueenCastle = true;
        }

        if(genericBoard.getCastling(GenericColor.BLACK, GenericCastling.KINGSIDE) != null) {
            blackKingCastle = true;
        }

        if(genericBoard.getCastling(GenericColor.BLACK, GenericCastling.QUEENSIDE) != null) {
            blackQueenCastle = true;
        }

        // set the en passe
        final GenericPosition ep = genericBoard.getEnPassant();

        if(ep != null) {
            int file = Arrays.asList(GenericFile.values()).indexOf(ep.file);
            int rank = Arrays.asList(GenericRank.values()).indexOf(ep.rank);

            this.enPassant = rank * 16 + file;
        }

        // compute the hash code
        computeHashCode();
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
    
    public static int createMoveValue(int fromSquare, int toSquare, char promotePiece) {
        final int pieceValue = AbstractPiece.pieceToPromoteValue(promotePiece);
        
        return fromSquare + (toSquare << 8) + (pieceValue << 16);  
    }
    
    public static int getFromSquare(int move) {
        return move & 0xFF;
    }

    public static int getToSquare(int move) {
        return (move >> 8) & 0xFF;
    }

    public static int getPromoteValue(int move) {
        return move >> 16;
    }

    public Piece getPiece(int square) {
        return this.board[square];
    }

    public Piece[] getBoard() {
        return board;
    }
    
    public void setState(State boardState) {
        this.whiteKingCastle = boardState.whiteKingCastle;
        this.whiteQueenCastle = boardState.whiteQueenCastle;
        this.blackKingCastle = boardState.blackKingCastle;
        this.blackQueenCastle = boardState.blackQueenCastle;
        this.enPassant = boardState.enPassant;
    }

    /**
     * @return the enPassant
     */
    public int getEnPassant() {
        return enPassant;
    }

    public boolean canKingCastle(Color color) {
        if(color.equals(Color.WHITE)) {
            return whiteKingCastle && board[0x05] == null && board[0x06] == null;
        } else {
            return blackKingCastle && board[0x75] == null && board[0x76] == null;
        }
    }

    public boolean canQueenCastle(Color color) {
        if(color.equals(Color.WHITE)) {
            return whiteQueenCastle && board[0x01] == null && board[0x02] == null && board[0x03] == null;
        } else {
            return blackQueenCastle && board[0x71] == null && board[0x72] == null && board[0x73] == null;
        }
    }

    public Color getActiveColor() {
    	return activeColor;
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

        whiteKingCastle = whiteQueenCastle = false;
        blackKingCastle = blackQueenCastle = false;
        enPassant = Board.MAX_SQUARE;

        // compute the new hash code
        computeHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for(int r=7; r >= 0; --r) {
            sb.append(r+1);
            sb.append(" ");
            for(int c = 0; c < 8; ++c) {
                Piece p = board[(r << 4) + c];

                sb.append(p == null ? "-" : p.toString());
                sb.append(" ");
            }
            sb.append(LINE_BREAK);
        }

        sb.append("  ");
        for(int i=0x61; i < 0x69; ++i) {
            sb.append((char)i);
            sb.append(" ");
        }

        sb.append(LINE_BREAK);

        return sb.toString();
    }

    /**
     * Moves the piece from one square to another.
     * @param move The move encoded as an integer.
     * @return The piece which was captured, or null if nothing was captured.
     * @throws IllegalMoveException
     */
    public State makeMove(int move) throws IllegalMoveException {
        final int fromSquare = Board.getFromSquare(move);
        final int toSquare = Board.getToSquare(move);
        final Piece fromPiece = board[fromSquare];

        if(fromPiece == null) {
            LOG.error("There is no piece on square: 0x{}", Integer.toHexString(fromSquare));
            throw new IllegalMoveException("There is no piece on square: 0x" + Integer.toHexString(fromSquare));
        }

        final State boardState = new State(this);

        // check to see if we're castling
        if(fromSquare == whiteKing && toSquare == 0x06 && canKingCastle(Color.WHITE)) {
            makeKingCastle(Color.WHITE);
            computeHashCode();
            return boardState;
        } else if(fromSquare == blackKing && toSquare == 0x76 && canKingCastle(Color.BLACK)) {
            makeKingCastle(Color.BLACK);
            computeHashCode();
            return boardState;
        } else if(fromSquare == whiteKing && toSquare == 0x02 && canQueenCastle(Color.WHITE)) {
            makeQueenCastle(Color.WHITE);
            computeHashCode();
            return boardState;
        } else if(fromSquare == blackKing && toSquare == 0x72 && canQueenCastle(Color.BLACK)) {
            makeQueenCastle(Color.BLACK);
            computeHashCode();
            return boardState;
        }

        // check to see if the move is legal or not (this covers en passe, but not castling)
        if(Arrays.binarySearch(fromPiece.generateAllMoves(this, fromSquare), toSquare) < 0) {
            LOG.error("Illegal move 0x{} - > 0x{} for {}", new String[] { Integer.toHexString(fromSquare), Integer.toHexString(toSquare), fromPiece.toString() } );
            LOG.error("CURRENT BOARD: {}{}", LINE_BREAK, this.toString());
            throw new IllegalMoveException("That move is not legal for " + fromPiece.toString());
        }

        final Piece toPiece = board[toSquare];
        
        // capture the piece
        if(toPiece != null) {
            // set the captured piece in the board's state
            boardState.setCapturedPiece(toPiece);

            // remove castling possibilities if it's a rook being captured
            if(toSquare == 0x00 && toPiece instanceof Rook) {
            	this.whiteQueenCastle = false;
            } else if(toSquare == 0x70 && toPiece instanceof Rook) {
            	this.blackQueenCastle = false;
            } else if(toSquare == 0x07 && toPiece instanceof Rook) {
            	this.whiteKingCastle = false;
            } else if(toSquare == 0x77 && toPiece instanceof Rook) {
            	this.blackKingCastle = false;
            }

            // remove the piece from the color's list
            if(toPiece.getColor().equals(Color.BLACK)) {
                ArraySet.removeNumber(blackPieces, toSquare, Board.MAX_SQUARE);
            } else {
                ArraySet.removeNumber(whitePieces, toSquare, Board.MAX_SQUARE);
            }

            board[toSquare] = null; // remove the piece from the board
        } else if(toSquare == enPassant) {
            if(fromPiece.getColor().equals(Color.BLACK)) {
                boardState.setCapturedPiece(board[toSquare + 0x10]);
                ArraySet.removeNumber(whitePieces, toSquare + 0x10, Board.MAX_SQUARE);
                board[toSquare + 0x10] = null;
            } else {
                boardState.setCapturedPiece(board[toSquare - 0x10]);
                ArraySet.removeNumber(blackPieces, toSquare - 0x10, Board.MAX_SQUARE);
                board[toSquare - 0x10] = null;
            }

            enPassant = Board.MAX_SQUARE;
        }
        
        // set the piece on the board
        final int promoteValue = Board.getPromoteValue(move);
        
        if(promoteValue != 0) {
            board[toSquare] = AbstractPiece.promoteValueToPiece(promoteValue, activeColor);
        } else {
            board[toSquare] = fromPiece;
        }
        
        board[fromSquare] = null;
        
        // we'll check below to see if this value should be set
        enPassant = Board.MAX_SQUARE;

        // update the piece's location
        if(fromPiece.getColor().equals(Color.WHITE)) {
            whitePieces[Arrays.binarySearch(whitePieces, fromSquare)] = toSquare;
            Arrays.sort(whitePieces);
            if(fromPiece instanceof King) {
                whiteKing = toSquare;
                whiteKingCastle = whiteQueenCastle = false;
            } else if(fromPiece instanceof Rook) {
                whiteKingCastle = whiteQueenCastle = false;
            } else if(fromPiece instanceof Pawn && (fromSquare & 0xF0) == 0x10 && (toSquare & 0xF0) == 0x30) {
            	enPassant = fromSquare + 0x10;
            }
        } else {
            blackPieces[Arrays.binarySearch(blackPieces, fromSquare)] = toSquare;
            Arrays.sort(blackPieces);
            if(fromPiece instanceof King) {
                blackKing = toSquare;
                blackKingCastle = blackQueenCastle = false;
            } else if(fromPiece instanceof Rook) {
                blackKingCastle = blackQueenCastle = false;
            } else if(fromPiece instanceof Pawn && (fromSquare & 0xF0) == 0x60 && (toSquare & 0xF0) == 0x40) {
            	enPassant = fromSquare - 0x10;
            }
        }

        fromPiece.pieceMoved(); // mark the piece has having moved

        // Switch the active color if we make a move on the board.
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;

        computeHashCode();  // re-compute the hash code

        return boardState;
    }

    public void unmakeMove(int move, State boardState) throws IllegalMoveException {
        final int fromSquare = Board.getFromSquare(move);
        final int toSquare = Board.getToSquare(move);
        final Piece toPiece = board[toSquare];

        if(toPiece == null) {
        	String from = Integer.toHexString(fromSquare);
        	String to = Integer.toHexString(toSquare);
        	
            LOG.error("Cannot unmake move {} -> {}, no piece on to square", from, to);
            throw new IllegalMoveException("There is no piece on square: 0x" + Integer.toHexString(toSquare));
        }

        // check to see if we're castling
        if(fromSquare == 0x04 && toSquare == 0x06 && whiteKing == 0x06) {
            board[0x04] = board[0x06];  // move the king back
            board[0x07] = board[0x05];  // move the rook back
            ArraySet.removeNumber(whitePieces, 0x06, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x05, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x04);
            ArraySet.addNumber(whitePieces, 0x07);
            whiteKing = 0x04;
            board[0x06] = board[0x05] = null;   // null these squares
            setState(boardState);
            computeHashCode();
            return;
        } else if(fromSquare == 0x74 && toSquare == 0x76 && blackKing == 0x76) {
            board[0x74] = board[0x76];  // move the king back
            board[0x77] = board[0x75];  // move the rook back
            ArraySet.removeNumber(blackPieces, 0x76, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x75, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x74);
            ArraySet.addNumber(blackPieces, 0x77);
            blackKing = 0x74;
            board[0x76] = board[0x75] = null;   // null these squares
            setState(boardState);
            computeHashCode();
            return;
        } else if(fromSquare == 0x04 && toSquare == 0x02 && whiteKing == 0x02) {
            board[0x04] = board[0x02];  // move the king back
            board[0x00] = board[0x03];  // move the rook back
            ArraySet.removeNumber(whitePieces, 0x03, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x02, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x04);
            ArraySet.addNumber(whitePieces, 0x00);
            whiteKing = 0x04;
            board[0x02] = board[0x03] = null;   // null these squares
            setState(boardState);
            computeHashCode();
            return;
        } else if(fromSquare == 0x74 && toSquare == 0x72 && blackKing == 0x72) {
            board[0x74] = board[0x72];  // move the king back
            board[0x70] = board[0x73];  // move the rook back
            ArraySet.removeNumber(blackPieces, 0x73, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x72, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x74);
            ArraySet.addNumber(blackPieces, 0x70);
            blackKing = 0x74;
            board[0x72] = board[0x73] = null;   // null these squares
            setState(boardState);
            computeHashCode();
            return;
        }
        
        final int promoteValue = Board.getPromoteValue(move);

        // check to see if we have a promotion, and swap the piece
        if(promoteValue != 0) {
            board[fromSquare] = new Pawn(toPiece.getColor());
        } else {
            // unmake the move
            board[fromSquare] = board[toSquare];
        }

        if(toPiece.getColor().equals(Color.WHITE)) {
            whitePieces[Arrays.binarySearch(whitePieces, toSquare)] = fromSquare;
            Arrays.sort(whitePieces);

            // if it was a king, reset it's position marker
            if(toPiece instanceof King) {
                whiteKing = fromSquare;
            }

            final Piece capturedPiece = boardState.getCapturedPiece();
            
            if(boardState.getEnPassant() == toSquare && toPiece instanceof Pawn) {
                board[toSquare - 0x10] = capturedPiece;
                ArraySet.addNumber(blackPieces, toSquare - 0x10);
                board[toSquare] = null;
            } else {
                board[toSquare] = capturedPiece;

                // if we captured a piece, add it back to the board
                if(capturedPiece != null) {
                    ArraySet.addNumber(blackPieces, toSquare);
                }
            }
        } else {
            blackPieces[Arrays.binarySearch(blackPieces, toSquare)] = fromSquare;
            Arrays.sort(blackPieces);

            // if it was a king, reset it's position marker
            if(toPiece instanceof King) {
                blackKing = fromSquare;
            }

            final Piece capturedPiece = boardState.getCapturedPiece();

            if(boardState.getEnPassant() == toSquare && toPiece instanceof Pawn) {
                board[toSquare + 0x10] = capturedPiece;
                ArraySet.addNumber(whitePieces, toSquare + 0x10);
                board[toSquare] = null;
            } else {
                board[toSquare] = capturedPiece;
                
                // if we captured a piece, add it back to the board
                if(capturedPiece != null) {
                    ArraySet.addNumber(whitePieces, toSquare);
                }
            }
        }
        
        // Switch the active color if we make a move on the board.
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;

        // reset the board's state
        setState(boardState);
    }

    public void makeKingCastle(Color color) throws IllegalMoveException {
        final int fromSquare = color.equals(Color.WHITE) ? 0x04 : 0x74;
        final int toSquare = color.equals(Color.WHITE) ? 0x06 : 0x76;

        // move the king
        board[toSquare] = board[fromSquare];
        board[fromSquare] = null;

        // move the rook
        board[toSquare - 1] = board[fromSquare + 3];
        board[fromSquare + 3] = null;

        // update the king, castling and pieces
        if(color.equals(Color.WHITE)) {
            whiteKing = toSquare;
            whiteKingCastle = whiteQueenCastle = false;
            ArraySet.removeNumber(whitePieces, 0x04, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x07, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x06);
            ArraySet.addNumber(whitePieces, 0x05);
        } else {
            blackKing = toSquare;
            blackKingCastle = blackQueenCastle = false;
            ArraySet.removeNumber(blackPieces, 0x74, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x77, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x76);
            ArraySet.addNumber(blackPieces, 0x75);
        }
    }

    public void makeQueenCastle(Color color) throws IllegalMoveException {
        final int fromSquare = color.equals(Color.WHITE) ? 0x04 : 0x74;
        final int toSquare = color.equals(Color.WHITE) ? 0x02 : 0x72;

        // move the king
        board[toSquare] = board[fromSquare];
        board[fromSquare] = null;

        // move the rook
        board[toSquare + 1] = board[fromSquare - 4];
        board[fromSquare - 4] = null;

        // update the king, castling and pieces
        if(color.equals(Color.WHITE)) {
            whiteKing = toSquare;
            whiteKingCastle = whiteQueenCastle = false;
            ArraySet.removeNumber(whitePieces, 0x04, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x00, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x02);
            ArraySet.addNumber(whitePieces, 0x03);
        } else {
            blackKing = toSquare;
            blackKingCastle = blackQueenCastle = false;
            ArraySet.removeNumber(blackPieces, 0x74, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x70, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x72);
            ArraySet.addNumber(blackPieces, 0x73);
        }
    }
    
    // TODO: DEBUG ONLY
    public void checkBoard() throws IllegalMoveException {
        int last = -1;
    	for(int p:whitePieces) {
            if(last > p) {
                throw new IllegalMoveException("WHITE PIECES OUT OF ORDER");
            }
            
            last = p;
            
    		if(p == Board.MAX_SQUARE) {
    			continue;
    		}
    		
    		if(board[p] == null) {
                System.out.println("BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
    		}
    		
    		if(!board[p].getColor().equals(Color.WHITE)) {
                System.out.println("BOARD PIECE IS NOT WHITE: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NOT WHITE: 0x" + Integer.toHexString(p));
    		}
    	}
    	
    	last = -1;

    	for(int p:blackPieces) {
            if(last > p) {
                throw new IllegalMoveException("BLACK PIECES OUT OF ORDER: " + last + " " + p);
            }
            
            last = p;
            
    		if(p == Board.MAX_SQUARE) {
    			continue;
    		}
    		
    		if(board[p] == null) {
                System.out.println("BLACK BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BLACK BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
    		}
    		
    		if(!board[p].getColor().equals(Color.BLACK)) {
                System.out.println("BOARD PIECE IS NOT BLACK: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NOT BLACK: 0x" + Integer.toHexString(p));
    		}
    	}
    	
    	for(int i=0; i < Board.MAX_SQUARE; ++i) {
    		if(board[i] == null) {
    			continue;
    		}
    		
    		if(Arrays.binarySearch(whitePieces, i) < 0 && Arrays.binarySearch(blackPieces, i) < 0) {
                throw new IllegalMoveException("BOARD PIECE NOT FOUND: 0x" + Integer.toHexString(i));
    		}
    	}
    }
    
    public boolean isInCheck(Color color) {
        return isInCheck(color.equals(Color.WHITE) ? whiteKing : blackKing);
    }

    /**
     * Given a king, checks to see if it is in check.
     * @param king The king to check.
     * @return True if the king is in check, false otherwise.
     */
    public boolean isInCheck(int kingPos) {
        final King king = (King) board[kingPos];
        
        if(king == null) {
        	System.out.println("KING IS NULL");
            System.out.println(this.toString());
        }
        
        final int[] pieces = king.getColor().equals(Color.WHITE) ? blackPieces : whitePieces;

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;
            }
            
            if(board[p] == null) {
                System.out.println("BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
            }
            
            if(Arrays.binarySearch(board[p].generateAllMoves(this, p), kingPos) >= 0) {
                return true;
            }
        }

        return false;
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
    
    public static final class State {
        private boolean whiteKingCastle = false;
        private boolean whiteQueenCastle = false;
        private boolean blackKingCastle = false;
        private boolean blackQueenCastle = false;

        private int enPassant = Board.MAX_SQUARE;
        private Piece capturedPiece;

        public State(Board board) {
            this.whiteKingCastle = board.whiteKingCastle;
            this.whiteQueenCastle = board.whiteQueenCastle;
            this.blackKingCastle = board.blackKingCastle;
            this.blackQueenCastle = board.blackQueenCastle;
            this.enPassant = board.enPassant;
        }

        /**
         * @return the capturedPiece
         */
        public Piece getCapturedPiece() {
            return capturedPiece;
        }

        /**
         * @param capturedPiece the capturedPiece to set
         */
        public void setCapturedPiece(Piece capturedPiece) {
            this.capturedPiece = capturedPiece;
        }

        /**
         * @return the whiteKingCastle
         */
        public boolean isWhiteKingCastle() {
            return whiteKingCastle;
        }

        /**
         * @return the whiteQueenCastle
         */
        public boolean isWhiteQueenCastle() {
            return whiteQueenCastle;
        }

        /**
         * @return the blackKingCastle
         */
        public boolean isBlackKingCastle() {
            return blackKingCastle;
        }

        /**
         * @return the blackQueenCastle
         */
        public boolean isBlackQueenCastle() {
            return blackQueenCastle;
        }

        /**
         * @return the enPassant
         */
        public int getEnPassant() {
            return enPassant;
        }
    }
}
