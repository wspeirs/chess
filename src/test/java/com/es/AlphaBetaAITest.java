package com.es;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Test;

import com.es.pieces.Pawn;
import com.es.pieces.Piece.Color;
import com.es.pieces.Queen;

public class AlphaBetaAITest {

    Board board = new Board();
    AlphaBetaAI alphaBeta = new AlphaBetaAI(Color.WHITE);
    NegaScoutAI nega = new NegaScoutAI(Color.WHITE);
    MoveAI normal = new MoveAI(Color.WHITE);

    static final int DEPTH = 1;

    public void setupBoard() {
        board.clearBoard();

        board.addPiece(new Pawn(Color.BLACK), 0x63);
//        board.addPiece(new Pawn(Color.BLACK), 0x64);
        board.addPiece(new Queen(Color.BLACK), 0x73);

        board.addPiece(new Pawn(Color.WHITE), 0x13);
//        board.addPiece(new Pawn(Color.WHITE), 0x14);
        board.addPiece(new Queen(Color.WHITE), 0x03);
    }

    @Test
    public void testAlphabeta() {
        LogManager.getRootLogger().setLevel(Level.DEBUG);

        //
        // setup alpha-beta
        //
        setupBoard();
        MoveNode alphaBetaNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

        long start = System.currentTimeMillis();
        int ret = alphaBeta.alphabeta(alphaBetaNode, DEPTH, -1000000, 10000000, Color.WHITE);
        alphaBetaNode.getBestChild();
        long alphaBetaTime = System.currentTimeMillis() - start;

        System.out.println("RET: " + ret);

        //
        // setup alpha-beta
        //
        setupBoard();
        MoveNode negaNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

        start = System.currentTimeMillis();
        ret = nega.negascout(negaNode, DEPTH, -1000000, 10000000, Color.WHITE);
        negaNode.getBestChild();
        long negaTime = System.currentTimeMillis() - start;

        System.out.println("RET: " + ret);

        //
        // setup normal
        //
        setupBoard();
        MoveNode normalNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

        start = System.currentTimeMillis();
        normal.computeNextMove(normalNode, Color.WHITE, DEPTH);
        long normalTime = System.currentTimeMillis() - start;

        //
        // print the results
        //
        System.out.println("* ALPHA BETA: " + alphaBetaTime);
        alphaBetaNode.printChildren();
        printMoves(alphaBetaNode.getBestChild());
        System.out.println();

        System.out.println("* NEGA: " + negaTime);
        negaNode.printChildren();
        printMoves(negaNode.getBestChild());
        System.out.println();

        System.out.println("* NORMAL: " + normalTime);
        normalNode.printChildren();
        printMoves(normalNode.getBestChild());
        System.out.println();
    }

    public void printMoves(MoveNode node) {
        while(true) {
            node.getBoard().printBoard();
            System.out.println("SCORE: " + normal.computeScore(node) + "=" + alphaBeta.computeScore(node) + " DEPTH: " + node.getDepth());

            if(node.getChildCount() == 0)
                break;
            node = node.getChildren().get(0);
            System.out.println();
        }
    }

}
