package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class QueenTest {

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
        Queen q = new Queen(Color.WHITE);

        int[] validMoves = new int[] { 0x00, 0x03, 0x06, 0x11, 0x13, 0x15, 0x22, 0x23, 0x24, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x42, 0x43, 0x44, 0x51, 0x53, 0x55, 0x60, 0x63, 0x66, 0x73, 0x77 };
        int[] moves = q.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesAllCaptures() {
        Queen b = new Queen(Color.WHITE);
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x22);
        board.addPiece(new Pawn(Color.BLACK), 0x23);
        board.addPiece(new Pawn(Color.BLACK), 0x24);
        board.addPiece(new Pawn(Color.BLACK), 0x32);
        board.addPiece(new Pawn(Color.BLACK), 0x34);
        board.addPiece(new Pawn(Color.BLACK), 0x42);
        board.addPiece(new Pawn(Color.BLACK), 0x43);
        board.addPiece(new Pawn(Color.BLACK), 0x44);

        int[] validMoves = new int[] { 0x22, 0x23, 0x24, 0x32, 0x34, 0x42, 0x43, 0x44 };
        int[] moves = b.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        Queen b = new Queen(Color.WHITE);
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x22);
        board.addPiece(new Pawn(Color.WHITE), 0x23);
        board.addPiece(new Pawn(Color.WHITE), 0x24);
        board.addPiece(new Pawn(Color.WHITE), 0x32);
        board.addPiece(new Pawn(Color.WHITE), 0x34);
        board.addPiece(new Pawn(Color.WHITE), 0x42);
        board.addPiece(new Pawn(Color.WHITE), 0x43);
        board.addPiece(new Pawn(Color.WHITE), 0x44);

        int[] validMoves = new int[] { };
        int[] moves = b.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesCaptureInCorner() {
        Queen b = new Queen(Color.WHITE);
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x60);
        board.addPiece(new Pawn(Color.BLACK), 0x00);
        board.addPiece(new Pawn(Color.BLACK), 0x03);
        board.addPiece(new Pawn(Color.BLACK), 0x30);
        board.addPiece(new Pawn(Color.BLACK), 0x37);
        board.addPiece(new Pawn(Color.BLACK), 0x77);
        board.addPiece(new Pawn(Color.BLACK), 0x06);

        int[] validMoves = new int[] { 0x00, 0x03, 0x06, 0x11, 0x13, 0x15, 0x22, 0x23, 0x24, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x42, 0x43, 0x44, 0x51, 0x53, 0x55, 0x60, 0x63, 0x66, 0x73, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x33);
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerLeftCorner() {
        Queen b = new Queen(Color.WHITE);

        int[] validMoves = new int[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x10, 0x11, 0x20, 0x22, 0x30, 0x33, 0x40, 0x44, 0x50, 0x55, 0x60, 0x66, 0x70, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x00);
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerRightCorner() {
        Queen b = new Queen(Color.WHITE);

        int[] validMoves = new int[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x16, 0x17, 0x25, 0x27, 0x34, 0x37, 0x43, 0x47, 0x52, 0x57, 0x61, 0x67, 0x70, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x07);
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperLeftCorner() {
        Queen b = new Queen(Color.WHITE);

        int[] validMoves = new int[] { 0x00, 0x07, 0x10, 0x16, 0x20, 0x25, 0x30, 0x34, 0x40, 0x43, 0x50, 0x52, 0x60, 0x61, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x70);
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperRightCorner() {
        Queen b = new Queen(Color.WHITE);

        int[] validMoves = new int[] { 0x00, 0x07, 0x11, 0x17, 0x22, 0x27, 0x33, 0x37, 0x44, 0x47, 0x55, 0x57, 0x66, 0x67, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76 };
        int[] moves = b.generateAllMoves(board, 0x77);
        
        verifyMoves(validMoves, moves);
    }
    
}
