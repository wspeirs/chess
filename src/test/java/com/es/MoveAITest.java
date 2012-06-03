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
        board.makeMove(0x52, 0x44);

        board.makeMove(0x15, 0x35);

        board.printBoard();

        MoveNode node = new MoveNode(board, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        MoveAI ai = new MoveAI(Color.BLACK);

        int[] move = ai.computeNextMove(node, Color.BLACK);

        board.makeMove(move[0], move[1], false);
        board.printBoard();

        System.out.println("* BEST *");
        node.printChildren();

        MoveNode c = node.getWorstChild();
        c.getBestChild().getBoard().printBoard();
        System.out.println("* WORST *");
        c.printChildren();

        c = node.getWorstChild().getBestChild();
        c.getWorstChild().getBoard().printBoard();
        System.out.println("* BEST *");
        c.printChildren();

        c = node.getWorstChild().getBestChild().getWorstChild();
        c.getBestChild().getBoard().printBoard();
        System.out.println("* WORST *");
        c.printChildren();
    }

}
