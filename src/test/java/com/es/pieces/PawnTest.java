package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class PawnTest {

    private Board board = new Board();

    @Before
    public void setup() {
        board.clearBoard();
    }
    
    public void verifyMoves(int[] validMoves, int[] moves) {
        int i = 0;
        for( ; i < validMoves.length; ++i) {
            assertEquals(validMoves[i], moves[i]);
        }
        
        for( ; i < moves.length; ++i) {
            assertEquals(Board.MAX_SQUARE, moves[i]);
        }
    }

    @Test
    public void testGenMovesSinglePiece() {
        Pawn b = new Pawn(Color.WHITE);

        int[] validMoves = new int[] { 0x43 };
        int[] moves = b.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSinglePieceDoubleStart() {
        Pawn b = new Pawn(Color.WHITE);

        int[] validMoves = new int[] { 0x23, 0x33 };
        int[] moves = b.generateAllMoves(board, 0x13);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        Pawn p = new Pawn(Color.WHITE);
        board.addPiece(p, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x42);
        board.addPiece(new Pawn(Color.BLACK), 0x43);
        board.addPiece(new Pawn(Color.BLACK), 0x44);

        int[] validMoves = new int[] { 0x42, 0x44 };
        int[] moves = p.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurrounded() {
        Pawn p = new Pawn(Color.WHITE);
        board.addPiece(p, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x43);

        int[] validMoves = new int[] { };
        int[] moves = p.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurroundedSingleMove() {
        Pawn p = new Pawn(Color.WHITE);
        board.addPiece(p, 0x13);
        board.addPiece(new Pawn(Color.WHITE), 0x23);

        int[] validMoves = new int[] { };
        int[] moves = p.generateAllMoves(board, 0x13);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurroundedDoubleMove() {
        Pawn p = new Pawn(Color.WHITE);
        board.addPiece(p, 0x13);
        board.addPiece(new Pawn(Color.WHITE), 0x33);

        int[] validMoves = new int[] { 0x23 };
        int[] moves = p.generateAllMoves(board, 0x13);
        
        verifyMoves(validMoves, moves);
    }

}
