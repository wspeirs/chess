package com.es.pieces;

import static org.junit.Assert.*;

import org.junit.Test;

import com.es.Board;

public class RookTest {
    
    private Board board = new Board();

    @Test
    public void testGenerateAllMoves() {
        Rook r = (Rook) board.getPiece(0x00);

        int[] moves = r.generateAllMoves();

        for(int m:moves) {
            assertEquals(Board.MAX_SQUARE, m);
        }
    }

}
