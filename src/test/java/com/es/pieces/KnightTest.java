package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class KnightTest {

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
        Knight k = new Knight(Color.WHITE, board, 0x33);

        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        Knight k = new Knight(Color.WHITE, board, 0x33);
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.BLACK, board, 0x12), 0x12);
        board.addPiece(new Pawn(Color.BLACK, board, 0x14), 0x14);
        board.addPiece(new Pawn(Color.BLACK, board, 0x21), 0x21);
        board.addPiece(new Pawn(Color.BLACK, board, 0x25), 0x25);
        board.addPiece(new Pawn(Color.BLACK, board, 0x41), 0x41);
        board.addPiece(new Pawn(Color.BLACK, board, 0x45), 0x45);
        board.addPiece(new Pawn(Color.BLACK, board, 0x52), 0x52);
        board.addPiece(new Pawn(Color.BLACK, board, 0x54), 0x54);

        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        Knight k = new Knight(Color.WHITE, board, 0x33);
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE, board, 0x12), 0x12);
        board.addPiece(new Pawn(Color.WHITE, board, 0x14), 0x14);
        board.addPiece(new Pawn(Color.WHITE, board, 0x21), 0x21);
        board.addPiece(new Pawn(Color.WHITE, board, 0x25), 0x25);
        board.addPiece(new Pawn(Color.WHITE, board, 0x41), 0x41);
        board.addPiece(new Pawn(Color.WHITE, board, 0x45), 0x45);
        board.addPiece(new Pawn(Color.WHITE, board, 0x52), 0x52);
        board.addPiece(new Pawn(Color.WHITE, board, 0x54), 0x54);

        int[] validMoves = new int[] { };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurrounded() {
        Knight k = new Knight(Color.WHITE, board, 0x33);
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE, board, 0x22), 0x22);
        board.addPiece(new Pawn(Color.WHITE, board, 0x23), 0x23);
        board.addPiece(new Pawn(Color.WHITE, board, 0x24), 0x24);
        board.addPiece(new Pawn(Color.WHITE, board, 0x32), 0x32);
        board.addPiece(new Pawn(Color.WHITE, board, 0x34), 0x34);
        board.addPiece(new Pawn(Color.WHITE, board, 0x42), 0x42);
        board.addPiece(new Pawn(Color.WHITE, board, 0x43), 0x43);
        board.addPiece(new Pawn(Color.WHITE, board, 0x44), 0x44);

        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        Knight k = new Knight(Color.WHITE, board, 0x00);

        int[] validMoves = new int[] { 0x12, 0x21 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerRightCorner() {
        Knight k = new Knight(Color.WHITE, board, 0x07);

        int[] validMoves = new int[] { 0x15, 0x26 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperLeftCorner() {
        Knight k = new Knight(Color.WHITE, board, 0x70);

        int[] validMoves = new int[] { 0x51, 0x62 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperRightCorner() {
        Knight k = new Knight(Color.WHITE, board, 0x77);

        int[] validMoves = new int[] { 0x56, 0x65 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

}
