package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.Board;


public class BishopTest {

    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        Bishop b = (Bishop) board.getPiece(0, 2);

        int[] moves = b.generateAllMoves();

        for(int m:moves) {
            assertEquals(Board.MAX_SQUARE, m);
        }
    }
}
