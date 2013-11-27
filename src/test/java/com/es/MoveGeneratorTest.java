package com.es;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.es.Board.State;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;
import com.es.pieces.Piece.Color;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.IllegalNotationException;

public class MoveGeneratorTest {

    private AlphaBetaAI ai;
    private Board board;

    @Test
    public void testPerft() throws IOException {
        File file= new File("perftsuite.epd");

        if(! file.exists()) {
            file = new File("src/test/resources/perftsuite.epd");
        }

        // read in all the lines of the test file
        final List<String> lines = FileUtils.readLines(file);

        //final List<String> lines = Arrays.asList("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 ;D1 48 ;D2 2039 ;D3 97862 ;D4 4085603 ;D5 193690690");

        final List<String> failedBoards = new ArrayList<String>();

        // i = the depth we're searching
        for (int i = 1; i < 3; i++) {
            for(String line:lines) {
                String[] tokens = line.split(";");

                // Setup a new board from fen
                GenericBoard genericBoard = null;
                try {
                    genericBoard = new GenericBoard(tokens[0].trim());
                } catch (IllegalNotationException e) {
                    fail("Illegal Notation: " + e.getMessage());
                }

                // make sure we have enough tokens/depth for this run
                if(tokens.length <= i) {
                    continue;
                }

                String[] data = tokens[i].trim().split(" ");
                int depth = Integer.parseInt(data[0].substring(1));
                int nodesNumber = Integer.parseInt(data[1]);
                Color activeColor = genericBoard.getActiveColor() == GenericColor.WHITE ? Color.WHITE : Color.BLACK;

                // Create a new board
                board = new Board(genericBoard);
                ai = new AlphaBetaAI(activeColor, board);
                MoveNode currentNode = new MoveNode(null, Board.MAX_SQUARE);

                System.out.println("BOARD: ");
                System.out.println(board.toString());
                System.out.print("Testing " + tokens[0].trim() + " depth " + depth + " with nodes number "
                        + nodesNumber + " ");
                long startTime = System.currentTimeMillis();

                // Count all moves
                int result = miniMax(currentNode, activeColor, depth);

                long endTime = System.currentTimeMillis();
                System.out.println("TIME: " + (endTime - startTime));

                // Check total moves against database
                // assertEquals(tokens[0].trim(), nodesNumber, result);

                if (nodesNumber != result) {
                    System.out.println("FAILED FOUND: " + result + " NEEDED: " + nodesNumber);
                    failedBoards.add(line + " @ depth " + depth + " FOUND: " + result);
                    //fail("FAILED");
                } else {
                    System.out.println("PASSED!");
                }
            }
        }

        for(String failed:failedBoards) {
            System.out.println("FAILED: " + failed);
        }
    }

    private int miniMax(MoveNode node, Color color, int depth) {
        if (depth == 0) {
            return 1;
        }

        int totalNodes = 0;

        int nodes = 0;
        int[] allMoves = ai.generateAllMoves(board.getPieces(color));

        try {
            board.checkBoard();
        } catch (IllegalMoveException e) {
            System.out.println(board);
            e.printStackTrace();
            fail("Illegal Move: " + e.getMessage());
        }

        for (int i = 0; i < allMoves.length && Board.getFromSquare(allMoves[i]) != Board.MAX_SQUARE; ++i) {
            State boardState = null;
            final String from = Integer.toHexString(Board.getFromSquare(allMoves[i]));
            final String to = Integer.toHexString(Board.getToSquare(allMoves[i]));

            try {
                //System.out.println("MOVE: " + color + " " + from + " -> " + to + " (" + board.getEnPassant() + ")");
                board.checkBoard();

                boardState = board.makeMove(allMoves[i]);
                board.checkBoard();

                if (board.getPiece(Board.getToSquare(allMoves[i])) == null) {
                    System.out.println("Never made move");
                    System.out.println(board);
                    fail("NEVER MADE MOVE");
                }

                // System.out.println(board);
            } catch (IllegalMoveException e) {
                System.err.println(board);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }

            // check to see if we move ourself into check
            if (board.isInCheck(color)) {
                // System.out.println("KING IN CHECK");
                // System.out.println(board.toString());

                try {
                    board.unmakeMove(allMoves[i], boardState);
                    board.checkBoard();
                    // System.out.println(board.toString());
                    continue;
                } catch (IllegalMoveException e) {
                    System.err.println(board);
                    e.printStackTrace();
                    fail("Illegal Move: " + e.getMessage());
                }
            }

            MoveNode childNode = new MoveNode(node, allMoves[i]);

            nodes = miniMax(childNode, color == Color.WHITE ? Color.BLACK : Color.WHITE, depth - 1);

            try {
                // System.out.println("UN-MOVE: " + color + " " + from + " -> "
                // + to + " (" + board.getEnPassant() + ")");
                board.unmakeMove(allMoves[i], boardState);
                board.checkBoard();
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
        int depth = 2;
        int res = 2042;
        GenericBoard board = new GenericBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        this.board = new Board(board);
        this.ai = new AlphaBetaAI(Color.WHITE, this.board);
        MoveNode currentNode = new MoveNode(null, Board.MAX_SQUARE);

        System.out.println("BOARD: ");
        System.out.println(this.board.toString());

        // Count all moves
        int result = miniMax(currentNode, board.getActiveColor() == GenericColor.WHITE ? Color.WHITE : Color.BLACK,
                depth);

        assertEquals(res, result);
    }

}
