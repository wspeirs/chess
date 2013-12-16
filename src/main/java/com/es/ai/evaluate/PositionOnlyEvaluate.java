package com.es.ai.evaluate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class PositionOnlyEvaluate implements IEvaluate {
    public static final Logger LOG = LoggerFactory.getLogger(PositionOnlyEvaluate.class);

    public PositionOnlyEvaluate() {
    }

    @Override
    public int evaluate(Board board, Color maxColor) {
        final int[] whitePieces = board.getPieces(Color.WHITE);
        final int[] blackPieces = board.getPieces(Color.BLACK);
        int whiteScore = 0;
        int blackScore = 0;

        //
        // Compute the value based upon position
        //
        for(int p:whitePieces) {
            if(p == Board.MAX_SQUARE) {
                break;
            }
            whiteScore += board.getPiece(p).getPositionValue(p);
        }

        for(int p:blackPieces) {
            if(p == Board.MAX_SQUARE) {
                break;
            }
            blackScore += board.getPiece(p).getPositionValue(p);
        }

/*
        if(LOG.isDebugEnabled()) {
            LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
            LOG.info("WHITE: {} BLACK: {} SCORE: " + (maxColor.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore), whiteScore, blackScore);
        }
*/

        return maxColor.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }
}