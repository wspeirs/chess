package com.es.pieces;

import org.junit.Test;

import com.es.pieces.Piece.Color;


public class BishopTest extends BasePieceTest {
    Bishop b = new Bishop(Color.WHITE);

    @Test
    public void testGenMovesSinglePiece() {
        int[] validMoves = new int[] { 0x00, 0x06, 0x11, 0x15, 0x22, 0x24, 0x42, 0x44, 0x51, 0x55, 0x60, 0x66, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x22);
        board.addPiece(new Pawn(Color.BLACK), 0x24);
        board.addPiece(new Pawn(Color.BLACK), 0x42);
        board.addPiece(new Pawn(Color.BLACK), 0x44);

        int[] validMoves = new int[] { 0x22, 0x24, 0x42, 0x44 };
        int[] moves = b.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x22);
        board.addPiece(new Pawn(Color.WHITE), 0x24);
        board.addPiece(new Pawn(Color.WHITE), 0x42);
        board.addPiece(new Pawn(Color.WHITE), 0x44);

        int[] validMoves = new int[] { };
        int[] moves = b.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesCaptureInCorner() {
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x60);
        board.addPiece(new Pawn(Color.BLACK), 0x00);
        board.addPiece(new Pawn(Color.BLACK), 0x77);
        board.addPiece(new Pawn(Color.BLACK), 0x06);

        int[] validMoves = new int[] { 0x00, 0x06, 0x11, 0x15, 0x22, 0x24, 0x42, 0x44, 0x51, 0x55, 0x60, 0x66, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSameInCorner() {
        board.addPiece(b, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x60);
        board.addPiece(new Pawn(Color.WHITE), 0x00);
        board.addPiece(new Pawn(Color.WHITE), 0x77);
        board.addPiece(new Pawn(Color.WHITE), 0x06);

        int[] validMoves = new int[] { 0x11, 0x15, 0x22, 0x24, 0x42, 0x44, 0x51, 0x55, 0x66 };
        int[] moves = b.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        int[] validMoves = new int[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77 };
        int[] moves = b.generateAllMoves(board, 0x00);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerRightCorner() {
        int[] validMoves = new int[] { 0x16, 0x25, 0x34, 0x43, 0x52, 0x61, 0x70 };
        int[] moves = b.generateAllMoves(board, 0x07);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperLeftCorner() {
        int[] validMoves = new int[] { 0x07, 0x16, 0x25, 0x34, 0x43, 0x52, 0x61 };
        int[] moves = b.generateAllMoves(board, 0x70);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperRightCorner() {
        int[] validMoves = new int[] { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66 };
        int[] moves = b.generateAllMoves(board, 0x77);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testPositionValueSymmetry() {
        super.testPositionValueSymmetry(new Bishop(Color.WHITE), new Bishop(Color.BLACK));
    }
}
