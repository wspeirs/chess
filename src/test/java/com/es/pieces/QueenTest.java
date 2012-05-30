package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.Board;

public class QueenTest {

    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        Queen q = (Queen) board.getPiece(0x03);

        int[] moves = q.generateAllMoves();

        for(int m:moves) {
            assertEquals(Board.MAX_SQUARE, m);
        }
    }

}
