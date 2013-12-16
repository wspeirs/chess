package com.es.ai.search;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.Board.State;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.ai.MoveNode;
import com.es.ai.evaluate.IEvaluate;
import com.es.pieces.Piece.Color;

/**
 * A simple minimax search.
 */
public class MiniMaxSearch extends AbstractSearch {
    public static final Logger LOG = LoggerFactory.getLogger(MiniMaxSearch.class);

    public MiniMaxSearch(Color colorPlaying, Board board, Configuration configuration, IEvaluate eval) {
        super(colorPlaying, board, configuration, eval);
    }

    @Override
    public MoveNode computeNextMove(MoveNode rootNode) throws IllegalMoveException {
        minimax(rootNode, configuration.getInt(CmdConfiguration.DEPTH), colorPlaying);

        return rootNode;
    }

    private int minimax(MoveNode node, int depth, Color currentPlayer) throws IllegalMoveException {
        if(depth == 0) // when we reach our depth, evaluate the board
            return eval.evaluate(board, colorPlaying);

        int bestValue = currentPlayer.equals(colorPlaying) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // generate all the moves for the current board
        final int[] moves = board.generateAllMoves();

        // add children for each move
        for(int move:moves) {
            if(move == Board.NULL_MOVE)
                break;

            node.addChild(move);
        }

        // go through the children computing scores
        for(MoveNode child:node.getChildren()) {
            final State state = board.makeMove(child.getMove()); // make this move

            // make the recursive minimax call
            final int value = minimax(child, depth-1, currentPlayer.inverse());

            // update the best value
            if(currentPlayer.equals(colorPlaying)) {
                bestValue = FastMath.max(bestValue, value);
            } else {
                bestValue = FastMath.min(bestValue, value);
            }

            board.unmakeMove(child.getMove(), state); // unmake the move
        }

        // set the score for the node
        node.setScore(bestValue);

        return bestValue;
    }
}
