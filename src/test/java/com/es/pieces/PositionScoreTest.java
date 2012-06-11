package com.es.pieces;

import static org.junit.Assert.*;

import org.junit.Test;

import com.es.Board;
import com.es.pieces.Piece.Color;

/**
 * 
 * <code>
 * 70 71 72 73 74 75 76 77 | 78 79 7a 7b 7c 7d 7e 7f
 * 60 61 62 63 64 65 66 67 | 68 69 6a 6b 6c 6d 6e 6f
 * 50 51 52 53 54 55 56 57 | 58 59 5a 5b 5c 5d 5e 5f
 * 40 41 42 43 44 45 46 47 | 48 49 4a 4b 4c 4d 4e 4f
 * 30 31 32 33 34 35 36 37 | 38 39 3a 3b 3c 3d 3e 3f
 * 20 21 22 23 24 25 26 27 | 28 29 2a 2b 2c 2d 2e 2f
 * 10 11 12 13 14 15 16 17 | 18 19 1a 1b 1c 1d 1e 1f
 * 00 01 02 03 04 05 06 07 | 08 09 0a 0b 0c 0d e0 0f
 * </code>
 * 
 */
public class PositionScoreTest {
    
    private TestPiece whitePiece = new TestPiece(Color.WHITE);
    private TestPiece blackPiece = new TestPiece(Color.BLACK);

    @Test
    public void testCorners() {
        assertEquals(-10, whitePiece.getPositionValue(0x70));
        assertEquals(-30, whitePiece.getPositionValue(0x77));
        assertEquals(-20, whitePiece.getPositionValue(0x00));
        assertEquals(-40, whitePiece.getPositionValue(0x07));

        assertEquals(-40, blackPiece.getPositionValue(0x70));
        assertEquals(-20, blackPiece.getPositionValue(0x77));
        assertEquals(-30, blackPiece.getPositionValue(0x00));
        assertEquals(-10, blackPiece.getPositionValue(0x07));
    }
    
    @Test
    public void testCenter() {
        assertEquals(10, whitePiece.getPositionValue(0x53));
        assertEquals(30, whitePiece.getPositionValue(0x54));
        assertEquals(20, whitePiece.getPositionValue(0x43));
        assertEquals(40, whitePiece.getPositionValue(0x44));

        assertEquals(70, blackPiece.getPositionValue(0x53));
        assertEquals(80, blackPiece.getPositionValue(0x54));
        assertEquals(60, blackPiece.getPositionValue(0x43));
        assertEquals(50, blackPiece.getPositionValue(0x44));
    }
    
    public static class TestPiece extends AbstractPiece {
        private static final int POSITION_VALUES[][] =
            { { -10,  0,  0,  0,  0,  0,  0,-30 },
              {   0,  0,  0,  0,  0,  0,  0,  0 },
              {   0, 10,  0, 10, 30,  0, 30,  0 },
              {   0,  0,  0, 20, 40,  0,  0,  0 },
              {   0,  0,  0, 50, 60,  0,  0,  0 },
              {   0, 20,  0, 80, 70,  0, 40,  0 },
              {   0,  0,  0,  0,  0,  0,  0,  0 },
              { -20,  0,  0,  0,  0,  0,  0,-40 }
            };

        public TestPiece(Color color) {
            super(color, POSITION_VALUES);
        }

        public int[] generateAllMoves(Board board, int curPos) {
            return null;
        }

        public int getValue() {
            return 0;
        }
        
    }

}
