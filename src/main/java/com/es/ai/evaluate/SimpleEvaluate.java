package com.es.ai.evaluate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;

public class SimpleEvaluate implements IEvaluate {
    public static final Logger LOG = LoggerFactory.getLogger(SimpleEvaluate.class);

    public SimpleEvaluate() {
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

        //
        // Add in bonus for each piece attacking and defending
        //
        //whiteScore += computeAttackDefendBonus(board, whitePieces, Color.BLACK);
        //blackScore += computeAttackDefendBonus(board, blackPieces, Color.WHITE);

/*
        if(LOG.isDebugEnabled()) {
            LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
            LOG.info("WHITE: {} BLACK: {} SCORE: " + (maxColor.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore), whiteScore, blackScore);
        }
*/

        return maxColor.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

    protected int computeAttackDefendBonus(Board board, int[] pieces, Color targetColor) {
        int ret = 0;

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;
            }

            final Piece piece = board.getPiece(p);
            final int[] moves = piece.generateAllMoves(board, p);

            for(int m:moves) {
                if(m == Board.MAX_SQUARE) {
                    break;
                }

                final Piece targetPiece = board.getPiece(m);

                if(targetPiece == null) {
                    continue;
                }

                if(targetPiece.getColor().equals(targetColor)) {
                    // check for an attack
                    ret += targetPiece.getValue() * .5;
                } else {
                    // check for a defense
                    ret += targetPiece.getValue() * 0.250;
                }
            }
        }

        return ret;
    }

}
