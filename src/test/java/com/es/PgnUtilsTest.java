package com.es;

import static org.junit.Assert.*;

import org.junit.Test;

import com.es.pieces.Piece.Color;

public class PgnUtilsTest {
    
    private Board board = new Board();
    private PgnUtils utils = new PgnUtils(board);

    @Test
    public void testParseSingleMove() throws Exception {
        int[] ret = utils.parseSingleMove(Color.WHITE, "Nc3");
        
        assertEquals(1, ret[0]);
        assertEquals(18, ret[1]);
    }

}
