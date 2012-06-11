package com.es;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class MoveAITest {

    private Board board = new Board();

    @Test
    public void testComputeScore() throws Exception {

        board.makeMove(0x13, 0x33);
        board.makeMove(0x33, 0x43);

        board.makeMove(0x71, 0x52);
//        board.makeMove(0x52, 0x44);

//        board.makeMove(0x15, 0x35);

        board.printBoard();

        MoveNode node = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        MoveAI ai = new MoveAI(Color.BLACK);

        long start = System.currentTimeMillis();
        int[] move = ai.computeNextMove(node, Color.BLACK);
        long end = System.currentTimeMillis();

        System.out.println("TIME: " + (end - start));

        board.makeMove(move[0], move[1], false);
        board.printBoard();

        System.out.println("MOVE: " + Integer.toHexString(move[0]) + " -> " + Integer.toHexString(move[1]));

        System.out.println("* NODE *");
        node.printChildren();

        System.out.println("* MOVES *");

        MoveNode c = node.getBestChild();

        while(true) {
            c.getBoard().printBoard();
            System.out.println("SCORE: " + ai.computeScore(c) + "DEPTH: " + c.getDepth());

//            c.printChildren();
//            move = c.getMove();
//            System.out.println(c.getScore() + ": " + Integer.toHexString(move[0]) + " -> " + Integer.toHexString(move[1]));

            c = c.getChildren().get(0);
            System.out.println();
        }

    }

}
