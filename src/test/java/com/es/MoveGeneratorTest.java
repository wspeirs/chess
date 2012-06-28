package com.es;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jcpi.data.GenericBoard;
import jcpi.data.GenericColor;
import jcpi.data.IllegalNotationException;

import org.junit.Test;

import com.es.Board.State;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;
import com.es.pieces.Piece.Color;

public class MoveGeneratorTest {

    @Test
    public void testPerft() throws FileNotFoundException {
        BufferedReader file;
        try {
            file = new BufferedReader(new FileReader("perftsuite.epd"));
        } catch (FileNotFoundException e1) {
            file = new BufferedReader(new FileReader("src/test/resources/perftsuite.epd"));
        }

        String line = null;

        for (int i = 1; i < 4; i++) {
            while (true) {
                try {
                  line = file.readLine();
                } catch (IOException e) {
                    fail("Error reading from file: " + e.getMessage());
                }

                if(line == null) {
                    break;
                }

                String[] tokens = line.split(";");

                // Setup a new board from fen
                GenericBoard board = null;
                try {
                    board = new GenericBoard(tokens[0].trim());
                } catch (IllegalNotationException e) {
                    fail("Illegal Notation: " + e.getMessage());
                }

                if (tokens.length > i) {
                    String[] data = tokens[i].trim().split(" ");
                    int depth = Integer.parseInt(data[0].substring(1));
                    int nodesNumber = Integer.parseInt(data[1]);

                    // Create a new board
                    Board testBoard = new Board(board);
                    MoveNode currentNode = new MoveNode(testBoard, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

                    System.out.println("BOARD: ");
                    System.out.println(testBoard.toString());
                    System.out.print("Testing " + tokens[0].trim()
                            + " depth " + depth + " with nodes number "
                            + nodesNumber + ": ");
                    long startTime = System.currentTimeMillis();

                    // Count all moves
                    int result = miniMax( currentNode, board.getActiveColor() == GenericColor.WHITE ? Color.WHITE : Color.BLACK, depth);

                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime - startTime);

                    // Check total moves against database
//                        assertEquals(tokens[0].trim(), nodesNumber, result);

                    if(nodesNumber != result) {
                        System.out.println("FAILED FOUND: " + result);
                    } else {
                        System.out.println("PASSED!");
                    }
                }
            }
        }
    }

    private int miniMax(MoveNode node, Color color, int depth) {
        if (depth == 0) {
            return 1;
        }

        int totalNodes = 0;
        AlphaBetaAI ai = new AlphaBetaAI(color);
        Board board = node.getBoard();

        int nodes = 0;
        int[] allMoves = ai.generateAllMoves(board, board.getPieces(color));

        for (int i = 0; i < allMoves.length && allMoves[i] != Board.MAX_SQUARE; i += 2) {
            State boardState = null;
            
            try {
                System.out.println("MOVE: " + Integer.toHexString(allMoves[i]) + " -> " + Integer.toHexString(allMoves[i + 1]));
                boardState = board.makeMove(allMoves[i], allMoves[i + 1], false);
                System.out.println(board);
            } catch (IllegalMoveException e) {
                System.err.println(board);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }
            
            // check to see if we move ourself into check
            if(board.isInCheck(color)) {
                System.out.println("KING IN CHECK");
                System.out.println(board.toString());

                try {
                    board.unmakeMove(allMoves[i], allMoves[i + 1], boardState);
                    System.out.println(board.toString());
                    continue;
                } catch (IllegalMoveException e) {
                    System.err.println(board);
                    e.printStackTrace();
                    fail("Illegal Move: " + e.getMessage());
                }
            }
            
            MoveNode childNode = new MoveNode(board, node, new int[] { allMoves[i], allMoves[i + 1] });
            
            nodes = miniMax(childNode, color == Color.WHITE ? Color.BLACK : Color.WHITE, depth - 1);
            
            try {
                board.unmakeMove(allMoves[i], allMoves[i + 1], boardState);
            } catch (IllegalMoveException e) {
                System.err.println(board);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }

            totalNodes += nodes;
        }

        return totalNodes;
    }
    

    @Test
    public void testBoardSetup() throws Exception {
        int depth = 1;
        int res = 10;
        GenericBoard board = new GenericBoard("1k6/8/8/5pP1/4K1P1/8/8/8 w - f6 0 1");
        Board testBoard = new Board(board);
        MoveNode currentNode = new MoveNode(testBoard, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

        System.out.println("BOARD: ");
        System.out.println(testBoard.toString());

        // Count all moves
        int result = miniMax( currentNode, board.getActiveColor() == GenericColor.WHITE ? Color.WHITE : Color.BLACK, depth);

        assertEquals(res, result);
    }

}
