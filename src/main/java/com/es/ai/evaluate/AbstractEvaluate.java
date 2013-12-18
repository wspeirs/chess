package com.es.ai.evaluate;

import com.es.Board;
import com.es.pieces.Piece.Color;

/**
 * An abstract evaluation class.
 */
public abstract class AbstractEvaluate {
    
    protected final Color colorPlaying;
    
    public AbstractEvaluate(Color colorPlaying) {
        this.colorPlaying = colorPlaying;
    }

    /**
     * The evaluation function where higher scores mean a better board for the color played.
     * @param board the board to evaluate.
     * @return The score for the board with respect to the color being played.
     */
    public abstract int evaluate(Board board);
}
