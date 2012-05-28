package com.es;

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

        board[6][0] = new Pawn(Color.BLACK, this, rowColToSquare(6,0));
        board[6][1] = new Pawn(Color.BLACK, this, rowColToSquare(6,1));
        board[6][2] = new Pawn(Color.BLACK, this, rowColToSquare(6,2));
        board[6][3] = new Pawn(Color.BLACK, this, rowColToSquare(6,3));
        board[6][4] = new Pawn(Color.BLACK, this, rowColToSquare(6,4));
        board[6][5] = new Pawn(Color.BLACK, this, rowColToSquare(6,5));
        board[6][6] = new Pawn(Color.BLACK, this, rowColToSquare(6,6));
        board[6][7] = new Pawn(Color.BLACK, this, rowColToSquare(6,7));

        // fill in white's pieces
        board[1][0] = new Pawn(Color.WHITE, this, rowColToSquare(1,0));
        board[1][1] = new Pawn(Color.WHITE, this, rowColToSquare(1,1));
        board[1][2] = new Pawn(Color.WHITE, this, rowColToSquare(1,2));
        board[1][3] = new Pawn(Color.WHITE, this, rowColToSquare(1,3));
        board[1][4] = new Pawn(Color.WHITE, this, rowColToSquare(1,4));
        board[1][5] = new Pawn(Color.WHITE, this, rowColToSquare(1,5));
        board[1][6] = new Pawn(Color.WHITE, this, rowColToSquare(1,6));
        board[1][7] = new Pawn(Color.WHITE, this, rowColToSquare(1,7));

        board[0][0] = new Rook(Color.WHITE,   this, rowColToSquare(0,0));
        board[0][1] = new Knight(Color.WHITE, this, rowColToSquare(0,1));
        board[0][2] = new Bishop(Color.WHITE, this, rowColToSquare(0,2));
        board[0][3] = new Queen(Color.WHITE,  this, rowColToSquare(0,3));
        board[0][4] = new King(Color.WHITE,   this, rowColToSquare(0,4));
        board[0][5] = new Bishop(Color.WHITE, this, rowColToSquare(0,5));
        board[0][6] = new Knight(Color.WHITE, this, rowColToSquare(0,6));
        board[0][7] = new Rook(Color.WHITE,   this, rowColToSquare(0,7));
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
}
