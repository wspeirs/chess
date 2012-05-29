package com.es;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BoardTest {

    private Board board = new Board();

    @Test
    public void testGetSquare() {
        for(int i=0; i < 64; ++i) {
            System.out.println(i + ": " + board.getPiece(i));
        }
    }

    @Test
    public void testPrintBoard() {
        board.printBoard();
    }

    @Test
    public void testMove() {
        boolean ret = board.makeMove(1, 18);

        assertTrue(ret);

        board.printBoard();
    }
}
