package com.es.pieces;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class RookTest {
    
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
        Rook r = new Rook(Color.WHITE, board, 0x33);

        int[] validMoves = new int[] { 0x03, 0x13, 0x23, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x43, 0x53, 0x63, 0x73 };
        int[] moves = r.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesAllCaptures() {
        Rook r = new Rook(Color.WHITE, board, 0x33);
        board.addPiece(r, 0x33);
        board.addPiece(new Pawn(Color.BLACK, board, 0x23), 0x23);
        board.addPiece(new Pawn(Color.BLACK, board, 0x32), 0x32);
        board.addPiece(new Pawn(Color.BLACK, board, 0x34), 0x34);
        board.addPiece(new Pawn(Color.BLACK, board, 0x43), 0x43);

        int[] validMoves = new int[] { 0x23, 0x32, 0x34, 0x43 };
        int[] moves = r.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        Rook b = new Rook(Color.WHITE, board, 0x33);
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.WHITE, board, 0x23), 0x23);
        board.addPiece(new Pawn(Color.WHITE, board, 0x32), 0x32);
        board.addPiece(new Pawn(Color.WHITE, board, 0x34), 0x34);
        board.addPiece(new Pawn(Color.WHITE, board, 0x43), 0x43);

        int[] validMoves = new int[] { };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesCaptureOnEdges() {
        Rook b = new Rook(Color.WHITE, board, 0x33);
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.BLACK, board, 0x03), 0x03);
        board.addPiece(new Pawn(Color.BLACK, board, 0x30), 0x30);
        board.addPiece(new Pawn(Color.BLACK, board, 0x37), 0x37);
        board.addPiece(new Pawn(Color.BLACK, board, 0x73), 0x73);

        int[] validMoves = new int[] { 0x03, 0x13, 0x23, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x43, 0x53, 0x63, 0x73 };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerLeftCorner() {
        Rook b = new Rook(Color.WHITE, board, 0x00);

        int[] validMoves = new int[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70 };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesLowerRightCorner() {
        Rook b = new Rook(Color.WHITE, board, 0x07);

        int[] validMoves = new int[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x17, 0x27, 0x37, 0x47, 0x57, 0x67, 0x77 };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperLeftCorner() {
        Rook b = new Rook(Color.WHITE, board, 0x70);

        int[] validMoves = new int[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77 };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
    @Test
    public void testGenMovesUpperRightCorner() {
        Rook b = new Rook(Color.WHITE, board, 0x77);

        int[] validMoves = new int[] { 0x07, 0x17, 0x27, 0x37, 0x47, 0x57, 0x67, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76 };
        int[] moves = b.generateAllMoves();
        
        verifyMoves(validMoves, moves);
    }
    
}
