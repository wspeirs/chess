package com.es;

import static org.junit.Assert.*;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class PgnUtilsTest {
    
    private Board board = new Board();
    private PgnUtils utils = new PgnUtils(board);

    @Test
    public void testParseSingleMoveWhite() throws Exception {
        int[] ret = utils.parseSingleMove(Color.WHITE, "Nc3");
        
        assertEquals(1, ret[0]);
        assertEquals(18, ret[1]);
    }

    @Test
    public void testParseSingleMoveBlackPawn() throws Exception {
        int[] ret = utils.parseSingleMove(Color.BLACK, "c5");
        
        assertEquals(50, ret[0]);
        assertEquals(34, ret[1]);
    }

}
