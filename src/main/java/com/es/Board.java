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
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericCastling;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericFile;
import com.fluxchess.jcpi.models.GenericPiece;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.GenericRank;

/**
 * A representation of a chess board.
 *
 * The layout is as follows where upper case is white and lower is black <code>
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
 * The queen's rook for white is [0][0], then [0][1] is the knight. The squares
 * are labeled: <code>
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
    public static final int NULL_MOVE = createMoveValue(MAX_SQUARE, MAX_SQUARE);

    private final Piece[] board;

    private final int[] blackPieces;
    private final int[] whitePieces;

    private int blackKing;
    private int whiteKing;

    private boolean whiteKingCastle = false;
    private boolean whiteQueenCastle = false;
    private boolean blackKingCastle = false;
    private boolean blackQueenCastle = false;

    private int enPassant = Board.MAX_SQUARE;

    private Color activeColor = Color.WHITE;

    private int hashCode;
    
    private int moves; // starts at 1 and increments after a black move

    /**
     * Constructs a new board with all the pieces in the starting position.
     */
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
        for (int i = 0x60; i < 0x68; ++i) {
            board[i] = new Pawn(Color.BLACK);
        }

        // fill in white's pawns
        for (int i = 0x10; i < 0x18; ++i) {
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
        for (int i = 0x00; i < board.length; ++i) {
            Piece p = board[i];

            if (p != null) {
                if (p.getColor().equals(Color.WHITE)) {
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

        // set the moves to 1
        moves = 1;
        
        // compute the hash code
        computeHashCode();
    }

    /**
     * Copy constructor for the board.
     * @param board the board to copy.
     */
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
        
        this.moves = board.moves;
    }

    /**
     * Constructs a {@link Board} object from a {@link GenericBoard}.
     * 
     * This is a copy constructor of sorts.
     * 
     * @param genericBoard the generic board to construct from.
     */
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
        if (genericBoard.getCastling(GenericColor.WHITE, GenericCastling.KINGSIDE) != null) {
            whiteKingCastle = true;
        }

        if (genericBoard.getCastling(GenericColor.WHITE, GenericCastling.QUEENSIDE) != null) {
            whiteQueenCastle = true;
        }

        if (genericBoard.getCastling(GenericColor.BLACK, GenericCastling.KINGSIDE) != null) {
            blackKingCastle = true;
        }

        if (genericBoard.getCastling(GenericColor.BLACK, GenericCastling.QUEENSIDE) != null) {
            blackQueenCastle = true;
        }

        // set the en passe
        final GenericPosition ep = genericBoard.getEnPassant();

        if (ep != null) {
            int file = Arrays.asList(GenericFile.values()).indexOf(ep.file);
            int rank = Arrays.asList(GenericRank.values()).indexOf(ep.rank);

            this.enPassant = rank * 16 + file;
        }
        
        // set the moves
        moves = genericBoard.getFullMoveNumber();

        // compute the hash code
        computeHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        }
        
        final Board arg = (Board) obj;

        // the boards are the same if the moves are the same and the boards are the same
        return arg.moves == moves && Arrays.equals(arg.board, this.board);
    }

    @Override
    public int hashCode() {
        if(hashCode == 0) {
            computeHashCode();
        }

        return hashCode;
    }

    /**
     * Given an integer, returns true if it's a valid position on the board.
     * @param pos the potential position.
     * @return true if the position is valid, false otherwise.
     */
    public static boolean isValidPosition(int pos) {
        if((pos & 0x08) != 0) return false;
        if(pos >= Board.MAX_SQUARE) return false;
        if(pos < 0) return false;

        return true;
    }

    /**
     * Computes a hash code for the board.
     */
    private void computeHashCode() {
        this.hashCode = 0;
        
        for (int i = 0; i < whitePieces.length; ++i) {
            this.hashCode ^= (whitePieces[i] << (16 + i / 2)) | (blackPieces[i] << (i / 2));
        }
        
        // TODO: include moves in this hash code
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

    public static int createMoveValue(int fromSquare, int toSquare) {
        return createMoveValue(fromSquare, toSquare, '-');
    }

    /**
     * The static version that converts a move to a String without considering pieces.
     * @param move the move to convert.
     * @return a string with the move.
     */
    public static String moveToString(int move) {
        final int from = getFromSquare(move);
        final int to = getToSquare(move);

        final StringBuilder sb = new StringBuilder();

        sb.append((char)(squareToCol(from) + 97));
        sb.append(squareToRow(from) + 1);
        sb.append("-");
        sb.append((char)(squareToCol(to) + 97));
        sb.append(squareToRow(to) + 1);

        return sb.toString();
    }

    public String moveToStringWithPieces(int move) {
        final int from = getFromSquare(move);
        final int to = getToSquare(move);

        final Piece fromPiece = board[from];
        final Piece toPiece = board[to];

        final StringBuilder sb = new StringBuilder();

        sb.append(fromPiece == null ? "?" : fromPiece.toString());
        sb.append(squareToString(to));
        sb.append(toPiece == null ? "-" : "*");
        sb.append(squareToString(from));

        return sb.toString();
    }

    public static String squareToString(int square) {
        if(! Board.isValidPosition(square))
            return null;

        final StringBuilder sb = new StringBuilder();

        sb.append((char)(squareToCol(square) + 97));
        sb.append(squareToRow(square) + 1);

        return sb.toString();
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
    
    public int getMoves() {
        return moves;
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
        // cannot castle out of check
        if(isInCheck(color)) {
            return false;
        }

        if (color.equals(Color.WHITE)) {
            // need to make sure boths spots are open, and that the King won't move through a spot that would put it in check
            return whiteKingCastle && board[0x05] == null && board[0x06] == null && (!isSquareAttacked(Color.WHITE, 0x05));
        } else {
            return blackKingCastle && board[0x75] == null && board[0x76] == null && (!isSquareAttacked(Color.BLACK, 0x75));
        }
    }

    public boolean canQueenCastle(Color color) {
        // cannot castle out of check
        if(isInCheck(color)) {
            return false;
        }

        if (color.equals(Color.WHITE)) {
            return whiteQueenCastle && board[0x01] == null && board[0x02] == null && board[0x03] == null && (!isSquareAttacked(Color.WHITE, 0x02)) && (!isSquareAttacked(Color.WHITE, 0x03));
        } else {
            return blackQueenCastle && board[0x71] == null && board[0x72] == null && board[0x73] == null && (!isSquareAttacked(Color.BLACK, 0x72)) && (!isSquareAttacked(Color.BLACK, 0x73));
        }
    }

    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * Removes all the pieces from the board. Useful for debugging.
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

        for (int r = 7; r >= 0; --r) {
            sb.append(r + 1);
            sb.append(" ");
            for (int c = 0; c < 8; ++c) {
                Piece p = board[(r << 4) + c];

                sb.append(p == null ? "-" : p.toString());
                sb.append(" ");
            }
            sb.append(LINE_BREAK);
        }

        sb.append("  ");
        for (int i = 0x61; i < 0x69; ++i) {
            sb.append((char) i);
            sb.append(" ");
        }

        sb.append(LINE_BREAK);

        return sb.toString();
    }

    public String toFEN() {
        final StringBuilder sb = new StringBuilder();

        // ref: http://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation

        // go through the pieces
        for(int start=0x70; start >= 0x00; start -= 0x10) {
            int blankCount = 0;

            for(int i=start; i < start+8; ++i) {
                final Piece p = board[i];

                if(p == null) {
                    ++blankCount;
                } else if(blankCount != 0) {
                    sb.append(blankCount);
                    sb.append(p.toString());
                    blankCount = 0;
                } else {
                    sb.append(p.toString());
                }
            }

            if(blankCount != 0)
                sb.append(blankCount);

            if(start != 0x00)
                sb.append("/");
        }

        // get the turn
        sb.append(' ');
        sb.append(this.getActiveColor().toFENString());

        // get the castling possibilities
        sb.append(' ');
        if(this.whiteKingCastle) sb.append('K');
        if(this.whiteQueenCastle) sb.append('Q');
        if(this.blackKingCastle) sb.append('k');
        if(this.blackQueenCastle) sb.append('q');
        if(sb.charAt(sb.length()-1) == ' ') sb.append('-'); // if we haven't appended, then put the -

        // get the en passant
        sb.append(' ');
        if(this.enPassant != MAX_SQUARE) {
            sb.append(Board.squareToString(this.enPassant));
        } else {
            sb.append('-');
        }

        // we punt on half moves for now
        sb.append(' ');
        sb.append(0);

        sb.append(' ');
        sb.append(getMoves());

        return sb.toString();
    }

    public GenericBoard toGenericBoard() {
        GenericBoard genericBoard = new GenericBoard();

        for (GenericPosition genericPosition : GenericPosition.values()) {
            int file = genericPosition.file.ordinal();
            int rank = genericPosition.rank.ordinal();
            int position = rank * 16 + file;

            Piece piece = board[position];
            if(piece != null) {
                GenericPiece genericPiece = GenericPiece.valueOf(piece.toString().charAt(0));
                genericBoard.setPiece(genericPiece, genericPosition);
            }
        }

        genericBoard.setActiveColor(GenericColor.valueOf(getActiveColor().toFENString().charAt(0)));

        if(this.whiteKingCastle) {
            genericBoard.setCastling(GenericColor.WHITE, GenericCastling.KINGSIDE, GenericFile.Fh);
        };
        if(this.whiteQueenCastle) {
            genericBoard.setCastling(GenericColor.WHITE, GenericCastling.QUEENSIDE, GenericFile.Fa);
        };
        if(this.blackKingCastle) {
            genericBoard.setCastling(GenericColor.BLACK, GenericCastling.KINGSIDE, GenericFile.Fh);
        };
        if(this.blackQueenCastle) {
            genericBoard.setCastling(GenericColor.BLACK, GenericCastling.QUEENSIDE, GenericFile.Fa);
        };

        if(this.enPassant != MAX_SQUARE) {
            genericBoard.setEnPassant(GenericPosition.valueOf(Board.squareToString(this.enPassant)));
        }

        genericBoard.setFullMoveNumber(getMoves());

        return genericBoard;
    }
    
    /**
     * Given the current board's state, generate all of the possible moves.
     * @return an array containing all possible moves for the board.
     */
    public int[] generateAllMoves() {
        final int[] pieces = activeColor.equals(Color.BLACK) ? blackPieces : whitePieces;
        final int[] allMoves = new int[161]; // make space for all possible moves
        int i = 0;

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;  // in sorted order, so we can break early
            }

            final Piece piece = getPiece(p);
            final int[] moves = piece.generateAllMoves(this, p);

            for(int m:moves) {
                if(m == Board.MAX_SQUARE) {
                    break;  // always in sorted order, so we're done here
                }

                // check to see if we have a pawn promoting
                if(piece instanceof Pawn && ( (m & 0xf0) == 0x70 || (m & 0xf0) == 0x00) ) {
                    final int move = Board.createMoveValue(p, m, 'q');

                    // if we don't have a valid move, then none of them will be valid
                    if(!isValidMove(activeColor, move)) {
                        break;
                    }

                    // otherwise, they'll all be valid
                    allMoves[i++] = move;
                    allMoves[i++] = Board.createMoveValue(p, m, 'b');
                    allMoves[i++] = Board.createMoveValue(p, m, 'n');
                    allMoves[i++] = Board.createMoveValue(p, m, 'r');
                } else {
                    final int move = Board.createMoveValue(p, m, '-');

                    if(isValidMove(activeColor, move)) {
                        allMoves[i++] = move;
                    }
                }
            }
        }

        Arrays.fill(allMoves, i, allMoves.length, NULL_MOVE);
        return allMoves;
    }

    /**
     * Checks to see if a move is valid by seeing if the King will be in check after the move.
     * @param kingColor the color of the king that just moved
     * @param move the move to test
     * @return true if the move won't leave the king in check, false if it will.
     */
    private boolean isValidMove(Color kingColor, int move) {
        boolean ret = true;

        try {
            final State state = makeMove(move, false);

            // we moved into check, so remove this position from the possible ones
            if(isInCheck(kingColor)) {
                ret = false;
            }

            this.unmakeMove(move, state);
        } catch (IllegalMoveException e) {
            LOG.error("Error making move that should be possible: {}", e.getMessage());
            LOG.error(board.toString());
        }

        return ret;
    }


    /**
     * Moves the piece from one square to another performing checks.
     *
     * @param move The move encoded as an integer.
     * @return The piece which was captured, or null if nothing was captured.
     * @throws IllegalMoveException
     */
    public State makeMove(int move) throws IllegalMoveException {
        return makeMove(move, true);
    }

    /**
     * Moves the piece from one square to another.
     *
     * @param move The move encoded as an integer.
     * @param checkMove Check to see if the move is legal or not.
     * @return The piece which was captured, or null if nothing was captured.
     * @throws IllegalMoveException
     */
    public State makeMove(int move, boolean checkMove) throws IllegalMoveException {
        final int fromSquare = Board.getFromSquare(move);
        final int toSquare = Board.getToSquare(move);
        final Piece fromPiece = board[fromSquare];

        if (fromPiece == null) {
            LOG.error("There is no piece on square: 0x{}", Integer.toHexString(fromSquare));
            throw new IllegalMoveException("There is no piece on square: 0x" + Integer.toHexString(fromSquare));
        }

        // we want to return the state of the board before the move was made
        final State boardState = new State(this);

        // check to see if we're castling
        if (fromSquare == whiteKing && toSquare == 0x06 && canKingCastle(Color.WHITE)) {
            makeKingCastle(Color.WHITE);
            moves++;
            computeHashCode();
            return boardState;
        } else if (fromSquare == blackKing && toSquare == 0x76 && canKingCastle(Color.BLACK)) {
            makeKingCastle(Color.BLACK);
            moves++;
            computeHashCode();
            return boardState;
        } else if (fromSquare == whiteKing && toSquare == 0x02 && canQueenCastle(Color.WHITE)) {
            makeQueenCastle(Color.WHITE);
            moves++;
            computeHashCode();
            return boardState;
        } else if (fromSquare == blackKing && toSquare == 0x72 && canQueenCastle(Color.BLACK)) {
            makeQueenCastle(Color.BLACK);
            moves++;
            computeHashCode();
            return boardState;
        }

        // check to see if the move is legal or not (this covers en passe, but not castling)
        if (checkMove && Arrays.binarySearch(fromPiece.generateAllMoves(this, fromSquare), toSquare) < 0) {
            LOG.error("Illegal move 0x{} - > 0x{} for {}", new String[] { Integer.toHexString(fromSquare), Integer.toHexString(toSquare), fromPiece.toString() });
            LOG.error("CURRENT BOARD: {}{}", LINE_BREAK, this.toString());
            throw new IllegalMoveException("That move is not legal for " + fromPiece.toString());
        }

        final Piece toPiece = board[toSquare];

        // capture the piece
        if (toPiece != null) {
            // set the captured piece in the board's state
            boardState.setCapturedPiece(toPiece);

            // remove castling possibilities if it's a rook being captured
            if (toSquare == 0x00 && toPiece instanceof Rook) {
                this.whiteQueenCastle = false;
            } else if (toSquare == 0x70 && toPiece instanceof Rook) {
                this.blackQueenCastle = false;
            } else if (toSquare == 0x07 && toPiece instanceof Rook) {
                this.whiteKingCastle = false;
            } else if (toSquare == 0x77 && toPiece instanceof Rook) {
                this.blackKingCastle = false;
            }

            // remove the piece from the color's list
            if (toPiece.getColor().equals(Color.BLACK)) {
                ArraySet.removeNumber(blackPieces, toSquare, Board.MAX_SQUARE);
            } else {
                ArraySet.removeNumber(whitePieces, toSquare, Board.MAX_SQUARE);
            }

            board[toSquare] = null; // remove the piece from the board
        } else if (toSquare == enPassant && fromPiece instanceof Pawn) {
            if (fromPiece.getColor().equals(Color.BLACK)) {
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

        final int promoteValue = Board.getPromoteValue(move);

        // make the move or the promotion
        if (promoteValue != 0) {
            board[toSquare] = AbstractPiece.promoteValueToPiece(promoteValue, activeColor);
        } else {
            board[toSquare] = fromPiece;
        }

        // remove the piece from the square where it started
        board[fromSquare] = null;

        // we'll check below to see if this value should be set
        enPassant = Board.MAX_SQUARE;

        // update the piece's location
        if (fromPiece.getColor().equals(Color.WHITE)) {
            whitePieces[Arrays.binarySearch(whitePieces, fromSquare)] = toSquare;
            Arrays.sort(whitePieces);

            if (fromPiece instanceof King) {
                whiteKing = toSquare;
                whiteKingCastle = whiteQueenCastle = false;
            } else if (fromSquare == 0x00 && fromPiece instanceof Rook) {
                whiteQueenCastle = false;
            } else if (fromSquare == 0x07 && fromPiece instanceof Rook) {
                whiteKingCastle = false;
            } else if (fromPiece instanceof Pawn && (fromSquare & 0xF0) == 0x10 && (toSquare & 0xF0) == 0x30) {
                enPassant = fromSquare + 0x10;
            }
        } else {
            blackPieces[Arrays.binarySearch(blackPieces, fromSquare)] = toSquare;
            Arrays.sort(blackPieces);

            if (fromPiece instanceof King) {
                blackKing = toSquare;
                blackKingCastle = blackQueenCastle = false;
            } else if (fromSquare == 0x70 && fromPiece instanceof Rook) {
                blackQueenCastle = false;
            } else if (fromSquare == 0x77 && fromPiece instanceof Rook) {
                blackKingCastle = false;
            } else if (fromPiece instanceof Pawn && (fromSquare & 0xF0) == 0x60 && (toSquare & 0xF0) == 0x40) {
                enPassant = fromSquare - 0x10;
            }
        }

        fromPiece.pieceMoved(); // mark the piece has having moved

        // Switch the active color if we make a move on the board.
        activeColor = activeColor.inverse();

        moves++;
        computeHashCode(); // re-compute the hash code

        return boardState;
    }

    public void unmakeMove(int move, State boardState) throws IllegalMoveException {
        final int fromSquare = Board.getFromSquare(move);
        final int toSquare = Board.getToSquare(move);
        final Piece toPiece = board[toSquare];

        if (toPiece == null) {
            String from = Integer.toHexString(fromSquare);
            String to = Integer.toHexString(toSquare);

            LOG.error("Cannot unmake move {} -> {}, no piece on to square", from, to);
            throw new IllegalMoveException("There is no piece on square: 0x" + Integer.toHexString(toSquare));
        }

        // check to see if we're castling
        if (fromSquare == 0x04 && toSquare == 0x06 && whiteKing == 0x06) {
            board[0x04] = board[0x06]; // move the king back
            board[0x07] = board[0x05]; // move the rook back
            ArraySet.removeNumber(whitePieces, 0x06, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x05, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x04);
            ArraySet.addNumber(whitePieces, 0x07);
            whiteKing = 0x04;
            board[0x06] = board[0x05] = null; // null these squares
            setState(boardState);
            activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
            moves--;
            computeHashCode();
            return;
        } else if (fromSquare == 0x74 && toSquare == 0x76 && blackKing == 0x76) {
            board[0x74] = board[0x76]; // move the king back
            board[0x77] = board[0x75]; // move the rook back
            ArraySet.removeNumber(blackPieces, 0x76, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x75, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x74);
            ArraySet.addNumber(blackPieces, 0x77);
            blackKing = 0x74;
            board[0x76] = board[0x75] = null; // null these squares
            setState(boardState);
            activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
            moves--;
            computeHashCode();
            return;
        } else if (fromSquare == 0x04 && toSquare == 0x02 && whiteKing == 0x02) {
            board[0x04] = board[0x02]; // move the king back
            board[0x00] = board[0x03]; // move the rook back
            ArraySet.removeNumber(whitePieces, 0x03, Board.MAX_SQUARE);
            ArraySet.removeNumber(whitePieces, 0x02, Board.MAX_SQUARE);
            ArraySet.addNumber(whitePieces, 0x04);
            ArraySet.addNumber(whitePieces, 0x00);
            whiteKing = 0x04;
            board[0x02] = board[0x03] = null; // null these squares
            setState(boardState);
            activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
            moves--;
            computeHashCode();
            return;
        } else if (fromSquare == 0x74 && toSquare == 0x72 && blackKing == 0x72) {
            board[0x74] = board[0x72]; // move the king back
            board[0x70] = board[0x73]; // move the rook back
            ArraySet.removeNumber(blackPieces, 0x73, Board.MAX_SQUARE);
            ArraySet.removeNumber(blackPieces, 0x72, Board.MAX_SQUARE);
            ArraySet.addNumber(blackPieces, 0x74);
            ArraySet.addNumber(blackPieces, 0x70);
            blackKing = 0x74;
            board[0x72] = board[0x73] = null; // null these squares
            setState(boardState);
            activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
            moves--;
            computeHashCode();
            return;
        }

        final int promoteValue = Board.getPromoteValue(move);

        // check to see if we have a promotion, and swap the piece
        if (promoteValue != 0) {
            board[fromSquare] = new Pawn(toPiece.getColor());
        } else {
            // unmake the move
            board[fromSquare] = board[toSquare];
        }

        if (toPiece.getColor().equals(Color.WHITE)) {
            whitePieces[Arrays.binarySearch(whitePieces, toSquare)] = fromSquare;
            Arrays.sort(whitePieces);

            // if it was a king, reset it's position marker
            if (toPiece instanceof King) {
                whiteKing = fromSquare;
            }

            final Piece capturedPiece = boardState.getCapturedPiece();

            if (boardState.getEnPassant() == toSquare && toPiece instanceof Pawn) {
                board[toSquare - 0x10] = capturedPiece;
                ArraySet.addNumber(blackPieces, toSquare - 0x10);
                board[toSquare] = null;
            } else {
                board[toSquare] = capturedPiece;

                // if we captured a piece, add it back to the board
                if (capturedPiece != null) {
                    ArraySet.addNumber(blackPieces, toSquare);
                }
            }
        } else {
            blackPieces[Arrays.binarySearch(blackPieces, toSquare)] = fromSquare;
            Arrays.sort(blackPieces);

            // if it was a king, reset it's position marker
            if (toPiece instanceof King) {
                blackKing = fromSquare;
            }

            final Piece capturedPiece = boardState.getCapturedPiece();

            if (boardState.getEnPassant() == toSquare && toPiece instanceof Pawn) {
                board[toSquare + 0x10] = capturedPiece;
                ArraySet.addNumber(whitePieces, toSquare + 0x10);
                board[toSquare] = null;
            } else {
                board[toSquare] = capturedPiece;

                // if we captured a piece, add it back to the board
                if (capturedPiece != null) {
                    ArraySet.addNumber(whitePieces, toSquare);
                }
            }
        }

        // Switch the active color if we make a move on the board.
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;

        // reset the board's state
        setState(boardState);
        
        moves--;
        computeHashCode();
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
        if (color.equals(Color.WHITE)) {
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

        this.enPassant = Board.MAX_SQUARE;
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
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
        if (color.equals(Color.WHITE)) {
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

        this.enPassant = Board.MAX_SQUARE;
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    /**
     * For debug use only.
     *
     * Checks the board by going through all the white and black pieces
     * and makes sure they're in order, not null, and the proper color.
     *
     * Finally the whole board is checked to makes sure all pieces are accounted for.
     * @throws IllegalMoveException
     */
    public void checkBoard() throws IllegalMoveException {
        int last = -1;

        // check the white pieces
        for (int p : whitePieces) {
            if (last > p) {
                throw new IllegalMoveException("WHITE PIECES OUT OF ORDER");
            }

            last = p;

            if (p == Board.MAX_SQUARE) {
                continue;
            }

            if (board[p] == null) {
                System.out.println("BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
            }

            if (!board[p].getColor().equals(Color.WHITE)) {
                System.out.println("BOARD PIECE IS NOT WHITE: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NOT WHITE: 0x" + Integer.toHexString(p));
            }
        }

        last = -1;
        // check the black pieces
        for (int p : blackPieces) {
            if (last > p) {
                throw new IllegalMoveException("BLACK PIECES OUT OF ORDER: " + last + " " + p);
            }

            last = p;

            if (p == Board.MAX_SQUARE) {
                continue;
            }

            if (board[p] == null) {
                System.out.println("BLACK BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BLACK BOARD PIECE IS NULL: 0x" + Integer.toHexString(p));
            }

            if (!board[p].getColor().equals(Color.BLACK)) {
                System.out.println("BOARD PIECE IS NOT BLACK: 0x" + Integer.toHexString(p));
                System.out.println(this.toString());
                throw new IllegalMoveException("BOARD PIECE IS NOT BLACK: 0x" + Integer.toHexString(p));
            }
        }

        for (int i = 0; i < Board.MAX_SQUARE; ++i) {
            if (board[i] == null) {
                continue;
            }

            if (Arrays.binarySearch(whitePieces, i) < 0 && Arrays.binarySearch(blackPieces, i) < 0) {
                throw new IllegalMoveException("BOARD PIECE NOT FOUND: 0x" + Integer.toHexString(i));
            }
        }
    }

    /**
     * Checks to see if a color is in check or not.
     * @param color the color to check.
     * @return True if that color's King is in check, false otherwise.
     */
    public boolean isInCheck(Color color) {
        return isSquareAttacked(color, color.equals(Color.WHITE) ? whiteKing : blackKing);
    }

    /**
     * Given a color and a square, see if the square is being attacked by the opposite color.
     *
     * @param defendingColor the color of the non-attacking player.
     * @param square the square to check.
     * @return True if the square is being attacked by the opposite color.
     */
    public boolean isSquareAttacked(Color defendingColor, int square) {

        // "look" to the left
        for(int pos=square-0x01; Board.isValidPosition(pos); pos -= 0x01) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square-0x01 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Rook || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" to the right
        for(int pos=square+0x01; Board.isValidPosition(pos); pos += 0x01) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square+0x01 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Rook || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" up
        for(int pos=square+0x10; Board.isValidPosition(pos); pos += 0x10) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square+0x10 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Rook || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" down
        for(int pos=square-0x10; Board.isValidPosition(pos); pos -= 0x10) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square-0x10 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Rook || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" upper-right
        for(int pos=square+0x11; Board.isValidPosition(pos); pos += 0x11) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square+0x11 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Bishop || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" lower-left
        for(int pos=square-0x11; Board.isValidPosition(pos); pos -= 0x11) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square-0x11 && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Bishop || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" upper-left
        for(int pos=square+0x0f; Board.isValidPosition(pos); pos += 0x0f) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square+0x0f && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Bishop || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // "look" lower-right
        for(int pos=square-0x0f; Board.isValidPosition(pos); pos -= 0x0f) {
            final Piece piece = board[pos];

            // no piece, keep moving
            if(piece == null) continue;

            // our own color, so done looking
            if(piece.getColor().equals(defendingColor)) break;

            // one away is a king
            if(pos == square-0x0f && piece instanceof King) return true;

            // we found a rook or queen of the other color, we're in check
            if(piece instanceof Bishop || piece instanceof Queen) {
                return true;
            } else {
                break; // non-attacking piece
            }
        }

        // check for knights attacking (adding)
        for(int knightMove:Knight.KNIGHT_MOVES) {
            final int pos = square + knightMove;

            if(! Board.isValidPosition(pos)) continue;

            final Piece piece = board[pos];

            // no piece, or our own
            if(piece == null || piece.getColor().equals(defendingColor)) continue;

            if(piece instanceof Knight) return true;
        }

        // check for knights attacking (subtracting)
        for(int knightMove:Knight.KNIGHT_MOVES) {
            final int pos = square - knightMove;

            if(! Board.isValidPosition(pos)) continue;

            final Piece piece = board[pos];

            // no piece, or our own
            if(piece == null || piece.getColor().equals(defendingColor)) continue;

            if(piece instanceof Knight) return true;
        }

        // check for pawns attacking
        if(defendingColor.equals(Color.BLACK)) {
            if(Board.isValidPosition(square - 0x11)) {
                final Piece piece = board[square - 0x11];

                if(piece != null &&
                   ! piece.getColor().equals(defendingColor) &&
                   piece instanceof Pawn) return true;
            }

            if(Board.isValidPosition(square - 0x0f)) {
                final Piece piece = board[square - 0x0f];

                if(piece != null &&
                   ! piece.getColor().equals(defendingColor) &&
                   piece instanceof Pawn) return true;
            }
        } else {
            if(Board.isValidPosition(square + 0x11)) {
                final Piece piece = board[square + 0x11];

                if(piece != null &&
                   ! piece.getColor().equals(defendingColor) &&
                   piece instanceof Pawn) return true;
            }

            if(Board.isValidPosition(square + 0x0f)) {
                final Piece piece = board[square + 0x0f];

                if(piece != null &&
                   ! piece.getColor().equals(defendingColor) &&
                   piece instanceof Pawn) return true;
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

        for (int p : pieces) {
            if (p != Board.MAX_SQUARE && board[p].getClass().equals(pieceType)) {
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
         * @param capturedPiece
         *            the capturedPiece to set
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
