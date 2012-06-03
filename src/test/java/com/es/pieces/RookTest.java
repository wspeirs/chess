package com.es.pieces;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class RookTest extends BasePieceTest {
    private Rook r = new Rook(Color.WHITE);

    @Test
    public void testGenMovesSinglePiece() {
        int[] validMoves = new int[] { 0x03, 0x13, 0x23, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x43, 0x53, 0x63, 0x73 };
        int[] moves = r.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllCaptures() {
        board.addPiece(r, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x23);
        board.addPiece(new Pawn(Color.BLACK), 0x32);
        board.addPiece(new Pawn(Color.BLACK), 0x34);
        board.addPiece(new Pawn(Color.BLACK), 0x43);

        int[] validMoves = new int[] { 0x23, 0x32, 0x34, 0x43 };
        int[] moves = r.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesAllSame() {
        board.addPiece(r, 0x33);
        board.addPiece(new Pawn(Color.WHITE), 0x23);
        board.addPiece(new Pawn(Color.WHITE), 0x32);
        board.addPiece(new Pawn(Color.WHITE), 0x34);
        board.addPiece(new Pawn(Color.WHITE), 0x43);

        int[] validMoves = new int[] { };
        int[] moves = r.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesCaptureOnEdges() {
        board.addPiece(r, 0x33);
        board.addPiece(new Pawn(Color.BLACK), 0x03);
        board.addPiece(new Pawn(Color.BLACK), 0x30);
        board.addPiece(new Pawn(Color.BLACK), 0x37);
        board.addPiece(new Pawn(Color.BLACK), 0x73);

        int[] validMoves = new int[] { 0x03, 0x13, 0x23, 0x30, 0x31, 0x32, 0x34, 0x35, 0x36, 0x37, 0x43, 0x53, 0x63, 0x73 };
        int[] moves = r.generateAllMoves(board, 0x33);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerLeftCorner() {
        int[] validMoves = new int[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70 };
        int[] moves = r.generateAllMoves(board, 0x00);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesLowerRightCorner() {
        int[] validMoves = new int[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x17, 0x27, 0x37, 0x47, 0x57, 0x67, 0x77 };
        int[] moves = r.generateAllMoves(board, 0x07);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperLeftCorner() {
        int[] validMoves = new int[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77 };
        int[] moves = r.generateAllMoves(board, 0x70);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testGenMovesUpperRightCorner() {
        int[] validMoves = new int[] { 0x07, 0x17, 0x27, 0x37, 0x47, 0x57, 0x67, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76 };
        int[] moves = r.generateAllMoves(board, 0x77);

        verifyMoves(validMoves, moves);
    }

    @Test
    public void testPositionValueSymmetry() {
        super.testPositionValueSymmetry(new Rook(Color.WHITE), new Rook(Color.BLACK));
    }
}
