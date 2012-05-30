package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class KingTest {
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
        King k = new King(Color.WHITE, board, 0x33);

        int[] validMoves = new int[] { 0x22, 0x23, 0x24, 0x32, 0x34, 0x42, 0x43, 0x44 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        King k = new King(Color.WHITE, board, 0x33);
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.BLACK, board, 0x22), 0x22);
        board.addPiece(new Pawn(Color.BLACK, board, 0x23), 0x23);
        board.addPiece(new Pawn(Color.BLACK, board, 0x24), 0x24);
        board.addPiece(new Pawn(Color.BLACK, board, 0x32), 0x32);
        board.addPiece(new Pawn(Color.BLACK, board, 0x34), 0x34);
        board.addPiece(new Pawn(Color.BLACK, board, 0x42), 0x42);
        board.addPiece(new Pawn(Color.BLACK, board, 0x43), 0x43);
        board.addPiece(new Pawn(Color.BLACK, board, 0x44), 0x44);

        int[] validMoves = new int[] { 0x22, 0x23, 0x24, 0x32, 0x34, 0x42, 0x43, 0x44 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        King k = new King(Color.WHITE, board, 0x33);
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE, board, 0x22), 0x22);
        board.addPiece(new Pawn(Color.WHITE, board, 0x23), 0x23);
        board.addPiece(new Pawn(Color.WHITE, board, 0x24), 0x24);
        board.addPiece(new Pawn(Color.WHITE, board, 0x32), 0x32);
        board.addPiece(new Pawn(Color.WHITE, board, 0x34), 0x34);
        board.addPiece(new Pawn(Color.WHITE, board, 0x42), 0x42);
        board.addPiece(new Pawn(Color.WHITE, board, 0x43), 0x43);
        board.addPiece(new Pawn(Color.WHITE, board, 0x44), 0x44);

        int[] validMoves = new int[] { };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        King k = new King(Color.WHITE, board, 0x00);

        int[] validMoves = new int[] { 0x01, 0x10, 0x11 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerRightCorner() {
        King k = new King(Color.WHITE, board, 0x07);

        int[] validMoves = new int[] { 0x06, 0x16, 0x17 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperLeftCorner() {
        King k = new King(Color.WHITE, board, 0x70);

        int[] validMoves = new int[] { 0x60, 0x61, 0x71 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperRightCorner() {
        King k = new King(Color.WHITE, board, 0x77);

        int[] validMoves = new int[] { 0x66, 0x67, 0x76 };
        int[] moves = k.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

}
