package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.es.Board;

public class KnightTest {

    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        Knight k = (Knight) board.getPiece(0x01);

        int[] moves = k.generateAllMoves();

        assertEquals(0x20, moves[0]);
        assertEquals(0x22, moves[1]);

        for(int i=2; i < moves.length; ++i) {
            assertEquals(Board.MAX_SQUARE, moves[i]);
        }
    }

}
