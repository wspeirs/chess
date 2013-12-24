package com.es.ai.evaluate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.pieces.Piece.Color;

public class PieceOnlyEvaluate extends AbstractEvaluate {
    public static final Logger LOG = LoggerFactory.getLogger(PieceOnlyEvaluate.class);

    public PieceOnlyEvaluate(Color colorPlaying) {
        super(colorPlaying);
    }

    @Override
    public int evaluate(Board board) {
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
            whiteScore += board.getPiece(p).getValue();
        }

        for(int p:blackPieces) {
            if(p == Board.MAX_SQUARE) {
                break;
            }
            blackScore += board.getPiece(p).getValue();
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }
}