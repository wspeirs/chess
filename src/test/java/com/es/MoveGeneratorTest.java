package com.es;

import static org.junit.Assert.assertEquals;
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
		for (int i = 1; i < 4; i++) {
			BufferedReader file;
			try {
				file = new BufferedReader(new FileReader("perftsuite.epd"));
			} catch (FileNotFoundException e1) {
				file = new BufferedReader(new FileReader("src/test/resources/perftsuite.epd"));
			}
			
			try {
				String line = file.readLine();
				while (line != null) {
					String[] tokens = line.split(";");
					
					// Setup a new board from fen
					GenericBoard board = new GenericBoard(tokens[0].trim());

					if (tokens.length > i) {
						String[] data = tokens[i].trim().split(" ");
						int depth = Integer.parseInt(data[0].substring(1));
						int nodesNumber = Integer.parseInt(data[1]);

						// Create a new board
						Board testBoard = new Board(board);
				        MoveNode currentNode = new MoveNode(testBoard, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

						System.out.print("Testing " + tokens[0].trim() + " depth " + depth + " with nodes number " + nodesNumber + ": ");
						long startTime = System.currentTimeMillis();
						
						// Count all moves
						int result = miniMax(currentNode, board.getActiveColor() == GenericColor.WHITE ? Color.WHITE : Color.BLACK, depth);
						
						long endTime = System.currentTimeMillis();
						System.out.println(endTime - startTime);
						
						// Check total moves against database
						assertEquals(tokens[0].trim(), nodesNumber, result);
					}

					line = file.readLine();
				}
			} catch (IOException e) {
				fail();
			} catch (IllegalNotationException e) {
				fail();
			} catch (IllegalMoveException e) {
				fail();
			}
		}
	}

	private int miniMax(MoveNode node, Color color, int depth) throws IllegalMoveException {
		if (depth == 0) {
			return 1;
		}

		int totalNodes = 0;
        AlphaBetaAI ai = new AlphaBetaAI(color);
        Board board = node.getBoard();

		int nodes = 0;
        int[] allMoves = ai.generateAllMoves(board, board.getPieces(color));
        for(int i = 0; i < allMoves.length; i += 2) {
			board.makeMove(allMoves[i], allMoves[i + 1]);
			Board moveBoard = new Board(node.getBoard());
            MoveNode childNode = new MoveNode(moveBoard, node, new int[] { allMoves[i],  allMoves[i + 1] });
			nodes = miniMax(childNode, color == Color.WHITE ? Color.BLACK : Color.WHITE, depth - 1);
			//board.unmakeMove(allMoves[i], allMoves[i + 1]);
			
			totalNodes += nodes;
		}

		return totalNodes;
	}

}
