package com.es;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
 * 56 57 59 59 60 61 62 63
 * 48 49 50 51 52 53 54 55
 * 40 41 42 43 44 45 46 47
 * 32 33 34 35 36 37 38 39
 * 24 25 26 27 28 29 30 31
 * 16 17 18 19 20 21 22 23
 * 08 09 10 11 12 13 14 15
 * 00 01 02 03 04 05 06 07
 * </code>
 *
 */
public class Board {

    public static final int MAX_ROW = 8;
    public static final int MAX_COL = 8;
    public static final int MAX_SQUARE = MAX_ROW * MAX_COL;

    private Piece[][] board = new Piece[MAX_ROW][MAX_COL];

    private Set<Piece> blackPieces = new HashSet<Piece>();
    private Set<Piece> blackCapturedPieces = new HashSet<Piece>();

    private Set<Piece> whitePieces = new HashSet<Piece>();
    private Set<Piece> whiteCapturedPieces = new HashSet<Piece>();

    public Board() {
        // fill in black's pieces
        board[7][0] = new Rook(Color.BLACK,   this, rowColToSquare(7,0));
        board[7][1] = new Knight(Color.BLACK, this, rowColToSquare(7,1));
        board[7][2] = new Bishop(Color.BLACK, this, rowColToSquare(7,2));
        board[7][3] = new Queen(Color.BLACK,  this, rowColToSquare(7,3));
        board[7][4] = new King(Color.BLACK,   this, rowColToSquare(7,4));
        board[7][5] = new Bishop(Color.BLACK, this, rowColToSquare(7,5));
        board[7][6] = new Knight(Color.BLACK, this, rowColToSquare(7,6));
        board[7][7] = new Rook(Color.BLACK,   this, rowColToSquare(7,7));

        for(int i=0; i < MAX_COL; ++i) {
            board[6][i] = new Pawn(Color.BLACK, this, rowColToSquare(6,i));
        }

        // add black's pieces to the set
        for(int i=0; i < MAX_COL; ++i) {
            blackPieces.add(board[7][i]);
            blackPieces.add(board[6][i]);
        }

        // fill in white's pieces
        for(int i=0; i < MAX_COL; ++i) {
            board[1][i] = new Pawn(Color.WHITE, this, rowColToSquare(1,i));
        }

        board[0][0] = new Rook(Color.WHITE,   this, rowColToSquare(0,0));
        board[0][1] = new Knight(Color.WHITE, this, rowColToSquare(0,1));
        board[0][2] = new Bishop(Color.WHITE, this, rowColToSquare(0,2));
        board[0][3] = new Queen(Color.WHITE,  this, rowColToSquare(0,3));
        board[0][4] = new King(Color.WHITE,   this, rowColToSquare(0,4));
        board[0][5] = new Bishop(Color.WHITE, this, rowColToSquare(0,5));
        board[0][6] = new Knight(Color.WHITE, this, rowColToSquare(0,6));
        board[0][7] = new Rook(Color.WHITE,   this, rowColToSquare(0,7));

        // add white's pieces to the set
        for(int i=0; i < MAX_COL; ++i) {
            whitePieces.add(board[0][i]);
            whitePieces.add(board[1][i]);
        }
    }

    public static int squareToRow(int square) {
        return square >> 3;
    }

    public static int squareToCol(int square) {
        return square & 0x07;
    }

    public static int rowColToSquare(int row, int col) {
        return (row << 3) + col;
    }

    public Piece getPiece(int row, int col) {
        return this.board[row][col];
    }

    public Piece getPiece(int square) {
        return this.board[square >> 3][square & 0x07];
    }

    public void printBoard() {
        for(int r=7; r >= 0; --r) {
            for(int c = 0; c < 8; ++c) {
                Piece p = board[r][c];

                System.out.print(p == null ? "-" : p.toString());
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece fromPiece = board[fromRow][fromCol];

        if(fromPiece == null) {
            return false;
        }

        // check to see if the move is legal or not
        int[] legalMoves = fromPiece.generateAllMoves();
        int toSquare = rowColToSquare(toRow, toCol);

        if(Arrays.binarySearch(legalMoves, toSquare) < 0) {
            return false;
        }

        Piece toPiece = board[toRow][toCol];

        if(toPiece != null) {
            Color c = toPiece.getColor();

            // remove it from the pieces on the board and add it to the captured pieces
            if(c.equals(Color.BLACK)) {
                blackPieces.remove(toPiece);
                blackCapturedPieces.add(toPiece);
            } else {
                whitePieces.remove(toPiece);
                whiteCapturedPieces.add(toPiece);
            }
        }

        board[toRow][toCol] = fromPiece;
        board[fromRow][fromCol] = null;

        return true;
    }

    public boolean makeMove(int fromSquare, int toSquare) {
        return makeMove(squareToRow(fromSquare), squareToCol(fromSquare), squareToRow(toSquare), squareToCol(toSquare));
    }
}
