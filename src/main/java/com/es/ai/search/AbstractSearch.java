package com.es.ai.search;

import org.apache.commons.configuration.Configuration;

import com.es.Board;
import com.es.IllegalMoveException;
import com.es.ai.MoveNode;
import com.es.ai.evaluate.AbstractEvaluate;
import com.es.pieces.Piece.Color;


/**
 * An abstract search used as the base for all other searches.
 */
public abstract class AbstractSearch {

    protected final Color colorPlaying;
    protected final Board board;
    protected final Configuration configuration;
    protected final AbstractEvaluate eval;

    public AbstractSearch(Color colorPlaying, Board board, Configuration configuration, AbstractEvaluate eval) {
        this.colorPlaying = colorPlaying;
        this.board = board;
        this.configuration = configuration;
        this.eval = eval;
    }

    /**
     * Computes the next move for the color the search is playing.
     * @param rootNode the root node of any existing tree.
     * @return the next move to make.
     */
    public abstract MoveNode computeNextMove(MoveNode rootNode) throws IllegalMoveException;
}
