package com.es.pieces;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.es.Board;

public class BasePieceTest {
    protected Board board = new Board();

    @Before
    public void setup() {
        board.clearBoard();
    }

    @Test
    public void test() {

    }

    public void verifyMoves(int[] validMoves, int[] moves) {
        int i = 0;
        for( ; i < validMoves.length; ++i) {
            assertEquals(validMoves[i], moves[i]);
        }

        for( ; i < moves.length; ++i) {
            assertEquals(Board.MAX_SQUARE, moves[i]);
        }
    }

    public void testPositionValueSymmetry(Piece white, Piece black) {
        for(int i=0; i < Board.MAX_SQUARE; ++i) {
            if((i & 0x80) != 0) {
                continue;
            }

            white.getPositionValue(i);
            black.getPositionValue(i);
        }
    }

}
