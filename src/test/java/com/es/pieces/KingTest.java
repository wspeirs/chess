package com.es.pieces;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class KingTest extends BasePieceTest {
    private King k = new King(Color.WHITE);

    @Test
    public void testGenMovesSinglePiece() {
        int[] validMoves = new int[] { 0x22, 0x23, 0x24, 0x32, 0x34, 0x42, 0x43, 0x44 };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x22);
        board.addPiece(new Pawn(Color.BLACK), 0x23);
        board.addPiece(new Pawn(Color.BLACK), 0x24);
        board.addPiece(new Pawn(Color.BLACK), 0x32);
        board.addPiece(new Pawn(Color.BLACK), 0x34);
        board.addPiece(new Pawn(Color.BLACK), 0x42);
        board.addPiece(new Pawn(Color.BLACK), 0x43);
        board.addPiece(new Pawn(Color.BLACK), 0x44);

        int[] validMoves = new int[] { 0x22, 0x23, 0x24, 0x32, 0x34, 0x42, 0x43, 0x44 };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x22);
        board.addPiece(new Pawn(Color.WHITE), 0x23);
        board.addPiece(new Pawn(Color.WHITE), 0x24);
        board.addPiece(new Pawn(Color.WHITE), 0x32);
        board.addPiece(new Pawn(Color.WHITE), 0x34);
        board.addPiece(new Pawn(Color.WHITE), 0x42);
        board.addPiece(new Pawn(Color.WHITE), 0x43);
        board.addPiece(new Pawn(Color.WHITE), 0x44);

        int[] validMoves = new int[] { };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        int[] validMoves = new int[] { 0x01, 0x10, 0x11 };
        int[] moves = k.generateAllMoves(board, 0x00);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerRightCorner() {
        int[] validMoves = new int[] { 0x06, 0x16, 0x17 };
        int[] moves = k.generateAllMoves(board, 0x07);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperLeftCorner() {
        int[] validMoves = new int[] { 0x60, 0x61, 0x71 };
        int[] moves = k.generateAllMoves(board, 0x70);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperRightCorner() {
        int[] validMoves = new int[] { 0x66, 0x67, 0x76 };
        int[] moves = k.generateAllMoves(board, 0x77);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testPositionValueSymmetry() {
        super.testPositionValueSymmetry(new King(Color.WHITE), new King(Color.BLACK));
    }
}
