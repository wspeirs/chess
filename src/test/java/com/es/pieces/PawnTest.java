package com.es.pieces;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class PawnTest extends BasePieceTest {
    private Pawn p = new Pawn(Color.WHITE);

    @Test
    public void testGenMovesSinglePiece() {
        int[] validMoves = new int[] { 0x43 };
        int[] moves = p.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSinglePieceDoubleStart() {
        int[] validMoves = new int[] { 0x23, 0x33 };
        int[] moves = p.generateAllMoves(board, 0x13);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
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
        board.addPiece(p, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x43);

        int[] validMoves = new int[] { };
        int[] moves = p.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurroundedSingleMove() {
        board.addPiece(p, 0x13);
        board.addPiece(new Pawn(Color.WHITE), 0x23);

        int[] validMoves = new int[] { };
        int[] moves = p.generateAllMoves(board, 0x13);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesSurroundedDoubleMove() {
        board.addPiece(p, 0x13);
        board.addPiece(new Pawn(Color.WHITE), 0x33);

        int[] validMoves = new int[] { 0x23 };
        int[] moves = p.generateAllMoves(board, 0x13);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testPositionValueSymmetry() {
        super.testPositionValueSymmetry(new Pawn(Color.WHITE), new Pawn(Color.BLACK));
    }
}
