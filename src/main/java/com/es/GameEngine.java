package com.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.es.pieces.Piece.Color;

public class GameEngine {

    public static void main(String[] args) throws IOException {
        Logger.getRootLogger().setLevel(Level.INFO);

        Board board = new Board();  // setup the board
        MoveNode currentNode = new MoveNode(board, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        MoveAI ai = new MoveAI(Color.BLACK);    // create the AI

        PgnUtils utils = new PgnUtils(board);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while(!(line = reader.readLine()).equals("q")) {
            try {
                int[] userMove = utils.parseSingleMove(Color.WHITE, line);

                board.makeMove(userMove[0], userMove[1]);
            } catch(IllegalMoveException e) {
                System.err.println("Illegal user move: " + e.getMessage());
                continue;
            }

            currentNode = ai.findNode(board);

            if(currentNode == null) {
                currentNode = new MoveNode(board, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
            } else {
                System.out.println("NODE FOUND");
            }

            long start = System.currentTimeMillis();
            int[] aiMove = ai.computeNextMove(currentNode, Color.BLACK);
            long time = System.currentTimeMillis() - start;

            currentNode.printChildren();
            System.out.println();

            String from = Integer.toHexString(aiMove[0]);
            String to = Integer.toHexString(aiMove[1]);
            System.out.println("MOVE (" + currentNode.getScore() + "): " + from + " -> " + to);

            double nodeCount = currentNode.getNodeCount();
            double nps = (nodeCount / (double)time) * 1000.0;

            System.out.println("TIME: " + time);
            System.out.println("NODES: " + nodeCount);
            System.out.println("NPS: " + nps);

            try {
                board.makeMove(aiMove[0], aiMove[1]);
            } catch (IllegalMoveException e) {
                System.err.println("Illegal computer move: " + e.getMessage());
                System.exit(-1);
            }

            board.printBoard();
        }

    }

}
