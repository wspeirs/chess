package com.es.pieces;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class KnightTest extends BasePieceTest {
    private Knight k = new Knight(Color.WHITE);

    @Test
    public void testGenMovesSinglePiece() {
        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x12);
        board.addPiece(new Pawn(Color.BLACK), 0x14);
        board.addPiece(new Pawn(Color.BLACK), 0x21);
        board.addPiece(new Pawn(Color.BLACK), 0x25);
        board.addPiece(new Pawn(Color.BLACK), 0x41);
        board.addPiece(new Pawn(Color.BLACK), 0x45);
        board.addPiece(new Pawn(Color.BLACK), 0x52);
        board.addPiece(new Pawn(Color.BLACK), 0x54);

        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x12);
        board.addPiece(new Pawn(Color.WHITE), 0x14);
        board.addPiece(new Pawn(Color.WHITE), 0x21);
        board.addPiece(new Pawn(Color.WHITE), 0x25);
        board.addPiece(new Pawn(Color.WHITE), 0x41);
        board.addPiece(new Pawn(Color.WHITE), 0x45);
        board.addPiece(new Pawn(Color.WHITE), 0x52);
        board.addPiece(new Pawn(Color.WHITE), 0x54);

        int[] validMoves = new int[] { };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurrounded() {
        board.addPiece(k, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x22);
        board.addPiece(new Pawn(Color.WHITE), 0x23);
        board.addPiece(new Pawn(Color.WHITE), 0x24);
        board.addPiece(new Pawn(Color.WHITE), 0x32);
        board.addPiece(new Pawn(Color.WHITE), 0x34);
        board.addPiece(new Pawn(Color.WHITE), 0x42);
        board.addPiece(new Pawn(Color.WHITE), 0x43);
        board.addPiece(new Pawn(Color.WHITE), 0x44);

        int[] validMoves = new int[] { 0x12, 0x14, 0x21, 0x25, 0x41, 0x45, 0x52, 0x54 };
        int[] moves = k.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        int[] validMoves = new int[] { 0x12, 0x21 };
        int[] moves = k.generateAllMoves(board, 0x00);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerRightCorner() {
        int[] validMoves = new int[] { 0x15, 0x26 };
        int[] moves = k.generateAllMoves(board, 0x07);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperLeftCorner() {
        int[] validMoves = new int[] { 0x51, 0x62 };
        int[] moves = k.generateAllMoves(board, 0x70);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperRightCorner() {
        int[] validMoves = new int[] { 0x56, 0x65 };
        int[] moves = k.generateAllMoves(board, 0x77);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testPositionValueSymmetry() {
        super.testPositionValueSymmetry(new Knight(Color.WHITE), new Knight(Color.BLACK));
    }
}
