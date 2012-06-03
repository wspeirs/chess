package com.es;


import java.util.HashMap;
import java.util.Map;

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
    public void testMap() {
        Map<Board, Integer> map = new HashMap<Board, Integer>();

        Board board1 = new Board();
        Board board2 = new Board(board1);

        map.put(board1, 1);
        map.put(board2, 2);
    }
}
