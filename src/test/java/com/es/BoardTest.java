package com.es;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.es.Board.State;
import com.es.pieces.Piece.Color;
import com.fluxchess.jcpi.models.GenericBoard;

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
    public void testToFEN() {
        System.out.println(board.toFEN());
        
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", board.toFEN());
    }

    @Test
    public void testToGenericBoard() {
        GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
        Board testBoard = new Board(genericBoard);

        assertEquals(genericBoard, testBoard.toGenericBoard());
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
    public void testUnmakeMove() throws IllegalMoveException {
        State boardState = board.makeMove(Board.createMoveValue(0x01, 0x22, '-'));

        System.out.println(board.toString());

        board.unmakeMove(Board.createMoveValue(0x01, 0x22, '-'), boardState);

        System.out.println(board.toString());
    }

    @Test
    public void inCheckPawn() throws Exception {
        Board board = new Board(new GenericBoard("1k6/P7/8/8/8/8/6Kp/8 b - - 0 1"));

        System.out.println(board.toString());
        assertTrue(board.isInCheck(Color.BLACK));
    }
}
