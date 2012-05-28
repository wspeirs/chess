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
 * rnbqkbnr
 * pppppppp
 * --------
 * --------
 * --------
 * --------
 * PPPPPPPP
 * RNBQKBNR
 * </code>
 * 
 * The queen's rook for white is [0][0], then [0][1] is the knight.
 *
 */
public class Board {

    public static final int MAX_ROW = 8;
    public static final int MAX_COL = 8;

    private Piece[][] board = new Piece[MAX_ROW][MAX_COL];

    public Board() {
        // fill in black's pieces
        board[7][0] = new Rook(Color.BLACK);
        board[7][1] = new Knight(Color.BLACK);
        board[7][2] = new Bishop(Color.BLACK);
        board[7][3] = new Queen(Color.BLACK);
        board[7][4] = new King(Color.BLACK);
        board[7][5] = new Bishop(Color.BLACK);
        board[7][6] = new Knight(Color.BLACK);
        board[7][7] = new Rook(Color.BLACK);

        board[6][0] = new Pawn(Color.BLACK);
        board[6][1] = new Pawn(Color.BLACK);
        board[6][2] = new Pawn(Color.BLACK);
        board[6][3] = new Pawn(Color.BLACK);
        board[6][4] = new Pawn(Color.BLACK);
        board[6][5] = new Pawn(Color.BLACK);
        board[6][6] = new Pawn(Color.BLACK);
        board[6][7] = new Pawn(Color.BLACK);

        // fill in white's pieces
        board[1][0] = new Pawn(Color.WHITE);
        board[1][1] = new Pawn(Color.WHITE);
        board[1][2] = new Pawn(Color.WHITE);
        board[1][3] = new Pawn(Color.WHITE);
        board[1][4] = new Pawn(Color.WHITE);
        board[1][5] = new Pawn(Color.WHITE);
        board[1][6] = new Pawn(Color.WHITE);
        board[1][7] = new Pawn(Color.WHITE);

        board[0][0] = new Rook(Color.WHITE);
        board[0][1] = new Knight(Color.WHITE);
        board[0][2] = new Bishop(Color.WHITE);
        board[0][3] = new Queen(Color.WHITE);
        board[0][4] = new King(Color.WHITE);
        board[0][5] = new Bishop(Color.WHITE);
        board[0][6] = new Knight(Color.WHITE);
        board[0][7] = new Rook(Color.WHITE);
    }
}
