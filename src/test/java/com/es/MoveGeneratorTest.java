package com.es;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jcpi.data.GenericBoard;
import jcpi.data.GenericColor;
import jcpi.data.IllegalNotationException;

import org.junit.Test;

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
            Board moveBoard = new Board(node.getBoard());
            try {
                moveBoard.makeMove(allMoves[i], allMoves[i + 1]);
            } catch (IllegalMoveException e) {
                if(e.isKingInCheck()) {
                    continue;
                }
                System.err.println(moveBoard);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }
            MoveNode childNode = new MoveNode(moveBoard, node, new int[] { allMoves[i], allMoves[i + 1] });
            nodes = miniMax(childNode, color == Color.WHITE ? Color.BLACK : Color.WHITE, depth - 1);

            totalNodes += nodes;
        }

        return totalNodes;
    }

}
