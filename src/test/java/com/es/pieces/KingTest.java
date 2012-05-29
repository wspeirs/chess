package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.Board;

public class KingTest {
    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        King k = (King) board.getPiece(0, 4);

        int[] moves = k.generateAllMoves();

        for(int m:moves) {
            assertEquals(Board.MAX_SQUARE, m);
        }
    }

}
