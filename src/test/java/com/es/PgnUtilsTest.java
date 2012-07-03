package com.es;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class PgnUtilsTest {

    private Board board = new Board();
    private PgnUtils utils = new PgnUtils(board);

    @Test
    public void testParseSingleMoveWhite() throws Exception {
        int[] ret = utils.parseSingleMove(Color.WHITE, "Nc3");

        assertEquals(0x01, ret[0]);
        assertEquals(0x22, ret[1]);
    }

    @Test
    public void testParseSingleMoveBlackPawn() throws Exception {
        int[] ret = utils.parseSingleMove(Color.BLACK, "c5");

        assertEquals(0x62, ret[0]);
        assertEquals(0x42, ret[1]);
    }

    @Test
    public void testComputePawnPgnMove() {
        String move = utils.computePgnMove(Board.createMoveValue(0x13, 0x23, '-'));

        assertEquals("Pd2d3", move);
    }

    @Test
    public void testComputeKnightCapturePgnMove() {
        String move = utils.computePgnMove(Board.createMoveValue(0x01, 0x12, '-'));

        assertEquals("Nb1xc2", move);
    }

}
