package com.es;


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.es.Board.State;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;
import com.es.pieces.Bishop;
import com.es.pieces.Knight;
import com.es.pieces.Pawn;
import com.es.pieces.Piece.Color;

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
        System.out.println(board.toString());
    }

    @Test
    public void testMap() {
        Map<Board, Integer> map = new HashMap<Board, Integer>();

        Board board1 = new Board();
        Board board2 = new Board(board1);

        map.put(board1, 1);
        map.put(board2, 2);
    }

    @Test
    public void testComputesScore() {
        board.clearBoard();

        board.addPiece(new Pawn(Color.WHITE), 0x13);
        board.addPiece(new Pawn(Color.WHITE), 0x34);
        board.addPiece(new Bishop(Color.WHITE), 0x63);
        board.addPiece(new Pawn(Color.BLACK), 0x64);
        board.addPiece(new Bishop(Color.BLACK), 0x75);

        board.addPiece(new Knight(Color.BLACK), 0x33);
        board.addPiece(new Knight(Color.WHITE), 0x01);

        System.out.println(board.toString());

        AlphaBetaAI ai = new AlphaBetaAI(Color.BLACK);

        System.out.println("SCORE: " + ai.computeScore(new MoveNode(board, null, new int[] { })));
    }
    
    @Test
    public void testUnmakeMove() throws IllegalMoveException {
        State boardState = board.makeMove(0x01, 0x22);
        
        System.out.println(board.toString());
        
        board.unmakeMove(0x01, 0x22, boardState);
        
        System.out.println(board.toString());
    }
}
