package com.es.pieces;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

public class King extends AbstractPiece {

    private static final Logger LOG = LoggerFactory.getLogger(Bishop.class);
    private static final int POSITION_VALUES[][] =
    { { -30,-40,-40,-50,-50,-40,-40,-30 },
      { -30,-40,-40,-50,-50,-40,-40,-30 },
      { -30,-40,-40,-50,-50,-40,-40,-30 },
      { -30,-40,-40,-50,-50,-40,-40,-30 },
      { -20,-30,-30,-40,-40,-30,-30,-20 },
      { -10,-20,-20,-20,-20,-20,-20,-10 },
      {  20, 20,  1,  1,  1,  1, 20, 20 },
      {  20, 30, 10,  1,  1, 10, 30, 20 }
    };

    public King(Color color) {
        super(color, POSITION_VALUES);
    }

    @Override
    public int getValue() {
        return 100000;
    }

    @Override
    public String toString() {
        if(getColor().equals(Color.BLACK)) {
            return "k";
        } else {
            return "K";
        }
    }

    @Override
    public int[] generateAllMoves(Board board, int curPos) {
        int[] ret = new int[10]; // can only ever move 8 positions
        int retIndex = 0;

        addPos(board, ret, retIndex++, curPos + 0x11); // check upper-right
        addPos(board, ret, retIndex++, curPos - 0x11); // check lower-left
        addPos(board, ret, retIndex++, curPos + 0x0f); // move to upper-left
        addPos(board, ret, retIndex++, curPos - 0x0f); // move to lower-right

        addPos(board, ret, retIndex++, curPos + 0x10); // move to forward
        addPos(board, ret, retIndex++, curPos - 0x10); // move to back
        addPos(board, ret, retIndex++, curPos + 0x01); // move to right
        addPos(board, ret, retIndex++, curPos - 0x01); // move to left

        if(board.canKingCastle(getColor())) {
            ret[retIndex++] = getColor().equals(Color.WHITE) ? 0x06 : 0x76;
        }

        if(board.canQueenCastle(getColor())) {
            ret[retIndex++] = getColor().equals(Color.WHITE) ? 0x02 : 0x72;
        }

        Arrays.fill(ret, retIndex, ret.length, Board.MAX_SQUARE);   // fill the rest with -1
        Arrays.sort(ret);   // sort the array

        return ret;
    }

}
