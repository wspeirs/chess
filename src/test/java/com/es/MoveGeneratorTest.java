package com.es;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.es.Board.State;
import com.es.pieces.AbstractPiece;
import com.es.pieces.Piece.Color;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericChessman;
import com.fluxchess.jcpi.models.GenericFile;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.GenericRank;
import com.fluxchess.jcpi.models.IllegalNotationException;
import com.fluxchess.jcpi.utils.MoveGenerator;

public class MoveGeneratorTest {

    private Board board;
    private int moveCount = 1;

    @Test
    public void testPerft() throws IOException {
        File file= new File("perftsuite.epd");

        if(! file.exists()) {
            file = new File("src/test/resources/perftsuite.epd");
        }

        // read in all the lines of the test file
        //final List<String> lines = FileUtils.readLines(file);

        final List<String> lines = Arrays.asList("1rq1kb2/2p5/p1P3Q1/1pP1p1Bp/N7/P4N2/1P3PP1/2R2RK1 b - - 0 1 ;D1 20 ;D2 400 ;D3 8902 ;D4 197281 ;D5 4865609 ;D6 119060324");

        final List<String> failedBoards = new ArrayList<String>();

        //System.in.read();

        // i = the depth we're searching
        for (int i = 1; i < 2; i++) {
            moveCount = 1;
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
                int numberOfNodes = Integer.parseInt(data[1]);
                Color activeColor = Color.fromGenericColor(genericBoard.getActiveColor());

                // Create a new board
                board = new Board(genericBoard);

                //System.out.println("BOARD: ");
                //System.out.println(board.toString());
                System.out.println("Testing " + tokens[0].trim() + " depth " + depth + " with nodes number " + numberOfNodes);

                // reset our move count before we start
                moveCount = 0;

                // compute the move tree
                miniMax(activeColor, depth);

                // Check total moves against database
                // assertEquals(tokens[0].trim(), nodesNumber, result);

                if (numberOfNodes != moveCount) {
                    System.out.println("FAILED FOUND: " + moveCount + " NEEDED: " + numberOfNodes);
                    //fail("FAILED");

                    long jcpiCount = MoveGenerator.perft(genericBoard, depth);
                    String message = findMissingMoves(board, depth);
                    if (message.isEmpty()) {
                        message = String.format("No problem found for board: %s%n", genericBoard);
                    }
                    System.out.print(message);

                    failedBoards.add(line + " @ depth " + depth + " FOUND: " + moveCount + " JCPI: " + jcpiCount);
                } else {
                    System.out.println("PASSED!");
                }
            }
        }

        for(String failed:failedBoards) {
            System.out.println("FAILED: " + failed);
        }
    }

    private void miniMax(Color color, int depth) {
        if (depth == 0) {
            return;
        }

        // generate all the moves
        final int[] allMoves = board.generateAllMoves();

        // go through all the moves, we stop when we see one that is Board.MAX_SQUARE
        for (int i = 0; i < allMoves.length && Board.getFromSquare(allMoves[i]) != Board.MAX_SQUARE; ++i) {
            State boardState = null;
            //final String from = Integer.toHexString(Board.getFromSquare(allMoves[i]));
            //final String to = Integer.toHexString(Board.getToSquare(allMoves[i]));

            try {
                //board.checkBoard();
                //System.out.println(moveCount++ + " : " + color + " " + board.moveToStringWithPieces(allMoves[i]) + " (" + board.getEnPassant() + ")");
                boardState = board.makeMove(allMoves[i]);
                //board.checkBoard();

                if(depth == 1) {
                    moveCount++;
                    //System.out.println(board.toFEN());
                }

                if (board.getPiece(Board.getToSquare(allMoves[i])) == null) {
                    System.out.println("Never made move");
                    System.out.println(board);
                    fail("NEVER MADE MOVE");
                }

                //System.out.println(board); // print out the board
            } catch (IllegalMoveException e) {
                System.err.println(board);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }

            // make the recursive call
            miniMax(color.inverse(), depth - 1);

            try {
                // System.out.println("UN-MOVE: " + color + " " + from + " -> " + to + " (" + board.getEnPassant() + ")");
                board.unmakeMove(allMoves[i], boardState);
                //board.checkBoard();
            } catch (IllegalMoveException e) {
                System.err.println(board);
                e.printStackTrace();
                fail("Illegal Move: " + e.getMessage());
            }
        }
    }

    private String findMissingMoves(Board board, int depth) {
        String message = "";

        // Get expected moves from JCPI
        GenericBoard genericBoard = board.toGenericBoard();
        Collection<GenericMove> expectedMoves = new HashSet<>(Arrays.asList(
            MoveGenerator.getGenericMoves(genericBoard)
        ));

        // Get actual moves
        int[] moves = board.generateAllMoves();
        Collection<GenericMove> actualMoves = new HashSet<>();
        for (int i = 0; i < moves.length && Board.getFromSquare(moves[i]) != Board.MAX_SQUARE; ++i) {
            int from = Board.getFromSquare(moves[i]);
            int to = Board.getToSquare(moves[i]);
            int promotion = Board.getPromoteValue(moves[i]);

            GenericFile file = GenericFile.values()[from % 16];
            GenericRank rank = GenericRank.values()[from >>> 4];
            GenericPosition positionFrom = GenericPosition.valueOf(file, rank);

            file = GenericFile.values()[to % 16];
            rank = GenericRank.values()[to >>> 4];
            GenericPosition positionTo = GenericPosition.valueOf(file, rank);

            GenericChessman genericChessman = null;
            if (promotion != 0) {
                genericChessman = GenericChessman.valueOfPromotion(AbstractPiece.promoteValueToPiece(promotion, board.getActiveColor()).toString().charAt(0));
            }
            
            actualMoves.add(new GenericMove(positionFrom, positionTo, genericChessman));
        }

        // Compare expected and actual moves
        Collection<GenericMove> illegalMoves = new HashSet<>(actualMoves);
        illegalMoves.removeAll(expectedMoves);

        Collection<GenericMove> missingMoves = new HashSet<>(expectedMoves);
        missingMoves.removeAll(actualMoves);

        if (illegalMoves.isEmpty() && missingMoves.isEmpty()) {
            if (depth <= 1) {
                return message;
            }

            for (int i = 0; i < moves.length && Board.getFromSquare(moves[i]) != Board.MAX_SQUARE; ++i) {
                State boardState = null;

                try {
                    boardState = board.makeMove(moves[i]);
                } catch (IllegalMoveException e) {
                    message += String.format("Error in makeMove() for board: %s%n", genericBoard);
                    message += String.format("%s%n", e.getMessage());
                }

                message += findMissingMoves(board, depth - 1);

                try {
                    board.unmakeMove(moves[i], boardState);
                } catch (IllegalMoveException e) {
                    message += String.format("Error in unmakeMove() for board: %s%n", genericBoard);
                    message += String.format("%s%n", e.getMessage());
                }

                if (!message.isEmpty()) {
                    break;
                }
            }
        } else {
            message += String.format("Failed check for board: %s%n", genericBoard);
            message += String.format("Expected: %s%n", expectedMoves);
            message += String.format("  Actual: %s%n", actualMoves);
            message += String.format(" Missing: %s%n", missingMoves);
            message += String.format(" Illegal: %s%n", illegalMoves);
        }

        return message;
    }

}
