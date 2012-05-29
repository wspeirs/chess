package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.Board;

public class KnightTest {

    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        Knight k = (Knight) board.getPiece(0, 1);

        int[] moves = k.generateAllMoves();

        assertEquals(16, moves[0]);
        assertEquals(18, moves[1]);

        for(int i=2; i < moves.length; ++i) {
            assertEquals(Board.MAX_SQUARE, moves[i]);
        }
    }

}
