package com.es.ai.search;

import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.es.Board;
import com.es.Board.State;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.ai.MoveNode;
import com.es.ai.evaluate.AbstractEvaluate;
import com.es.pieces.Piece.Color;


public class NegaScoutSearch extends AbstractSearch {

    private static final Logger LOG = LoggerFactory.getLogger(NegaScoutSearch.class);
    
    public NegaScoutSearch(Color colorPlaying, Board board, Configuration configuration, AbstractEvaluate eval) {
        super(colorPlaying, board, configuration, eval);
    }

    @Override
    public MoveNode computeNextMove(MoveNode rootNode) throws IllegalMoveException {
        int ret = negamax(rootNode, configuration.getInt(CmdConfiguration.DEPTH), Integer.MIN_VALUE, Integer.MAX_VALUE, colorPlaying);

        LOG.debug("RET: {}", ret);
        
        return rootNode;
    }

    private int negamax(MoveNode node, int depth, int alpha, int beta, Color currentPlayer) throws IllegalMoveException {
        if(depth == 0) { // when we reach our depth, evaluate the board
            final int score = -eval.evaluate(board);
            node.setScore(score);
            return score * (currentPlayer.equals(colorPlaying) ? 1 : -1);
        }

        if(node.getChildCount() == 0) {
            // generate all the moves for the current board
            final int[] moves = board.generateAllMoves();
    
            // add children for each move
            for(int move:moves) {
                if(move == Board.NULL_MOVE)
                    break;
    
                node.addChild(currentPlayer, move);
            }
        }
        
        final List<MoveNode> children = node.getChildren();

        // go through the children computing scores
        for(int i=0; i < node.getChildCount(); ++i) {
            final MoveNode child = children.get(i);
            final State state = board.makeMove(child.getMove()); // make this move
            
            int value = -negamax(child, depth-1, -beta, -alpha, currentPlayer.inverse());

            if(i != 0 && alpha < value && value < beta) {
                alpha = -negamax(child, depth-1, -beta, -value, currentPlayer.inverse());
            }
            
            board.unmakeMove(child.getMove(), state); // unmake the move
            
            alpha = FastMath.max(alpha, value); // update the best value
            
            if(alpha >= beta) {
                break;
            }
            
            beta = alpha + 1;
        }

        node.setScore(alpha); // set the score for the node

        return alpha;
    }
}
