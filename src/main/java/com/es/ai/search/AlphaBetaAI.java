package com.es.ai.search;


import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.Board.State;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.ai.MoveNode;
import com.es.ai.TranspositionTable;
import com.es.pieces.Piece.Color;

public class AlphaBetaAI {
    private static final Logger LOG = LoggerFactory.getLogger(AlphaBetaAI.class);

    private final TranspositionTable transpositionTable = new TranspositionTable();
    private int transHit = 0;
    private final Color colorPlaying;
    private final Configuration configuration;
    private final int depth;
    private final Board board;

    public AlphaBetaAI(Color colorPlaying, Board board, Configuration configuration) {
        this.colorPlaying = colorPlaying;
        this.board = board;
        this.configuration = configuration;
        this.depth = configuration.getInt(CmdConfiguration.DEPTH);
    }

    public AlphaBetaAI(Color colorPlaying, Board board) {
        this.colorPlaying = colorPlaying;
        this.board = board;
        this.configuration = null;
        this.depth = 4;
    }

    public int getTransHit() {
        return transHit;
    }

    public int computeNextMove(MoveNode node, Color color) {
        for(int d=2; d <= 4; d++) {
            transHit = 0;
            long start = System.currentTimeMillis();
            alphabeta(node, d, -1000000, 1000000, color);

            try {
                board.checkBoard();
            } catch (IllegalMoveException e) {
                LOG.error("Error with board: {}", e.getMessage());
            }

            long time = System.currentTimeMillis() - start;

            System.out.println("DEPTH: " + d + " TT HITS: " + transHit + " TIME: " + time + " NODES: " + node.getNodeCount() + " CHILD: " + node.getChildCount());
            System.out.println(node.childrenToString());
        }

        return node.getBestChild().getMove();
    }

    public int alphabeta(MoveNode node, int depth, int alpha, int beta, Color color) {
        if(depth == 0) {
            int score = computeScore();
            node.setScore(score);
            node.setRetVal(score);
            return score;
        }

        int[] allMoves = board.generateAllMoves();
        int[] ret = { 0, alpha, beta };


        for(int i = 0; i < allMoves.length && Board.getFromSquare(allMoves[i]) != Board.MAX_SQUARE; ++i) {

            final MoveNode child = node.findChild(allMoves[i]);

            if(child != null) {
                int score = alphabeta(child, depth - 1, alpha, beta, color.inverse());

                // get the alpha or beta depending upon who's turn it is
                if(colorPlaying.equals(color)) {
                    alpha = Math.max(alpha, score);
                } else {
                    beta = Math.min(beta, score);
                }

            } else {
                // compute alpha-beta for the move
                ret = alphabeta(node, depth, allMoves[i], alpha, beta, color);

                // update the values of alpha and beta
                alpha = ret[0];
                beta = ret[1];
            }

            if(beta <= alpha) {
                // node.removeChildrenAfter(child);
                node.removeChildrenDeeperThan(depth-1);
                break;
            }
        }

        final int retVal = colorPlaying.equals(color) ? alpha : beta;

        int score = retVal;

        if(node.getChildCount() != 0) {
            score = (colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore();
        }

        node.setScore(score);
        node.setRetVal(retVal);

        return retVal;
    }


    public int[] alphabeta(MoveNode node, int depth, int move, int alpha, int beta, Color color) {
        State state;

        try {
            state = board.makeMove(move);
        } catch (IllegalMoveException e) {
            if(!e.isKingInCheck()) {
                LOG.error("Illegal move during compute: {}", e.getMessage());
                System.out.println(board.toString());
                System.exit(-1);
            }

            // return here, but continue searching
            return new int[] { alpha, beta };
        }

        MoveNode childNode = transpositionTable.get(board);

        // check to see if we need to create a new node, or if we can use the one from the table
        if(childNode == null || childNode.getDepth() <= depth) {
            childNode = node.addChild(move);

            final int score = alphabeta(childNode, depth - 1, alpha, beta, color.inverse());

            // get the alpha or beta depending upon who's turn it is
            if(colorPlaying.equals(color)) {
                alpha = Math.max(alpha, score);
            } else {
                beta = Math.min(beta, score);
            }

            // add it to the transposition table
            transpositionTable.put(board, childNode);
        } else {
            if(colorPlaying.equals(color)) {
                alpha = Math.max(alpha, childNode.getRetVal());
            } else {
                beta = Math.min(beta, childNode.getRetVal());
            }

            transHit++;

            // add the child node's children to the current node's children
            node.addChildren(childNode);
        }

        // unmake the move
        try {
            board.unmakeMove(move, state);
        } catch (IllegalMoveException e) {
            LOG.error("Illegal move during compute: {}", e.getMessage());
            System.out.println(board.toString());
            System.exit(-1);
        }

        // see if we have a cut-off
        if(beta <= alpha) {
            node.setRetVal(colorPlaying.equals(color) ? alpha : beta);
            if(node.getChildCount() != 0) {
                node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
            } else {
                node.setScore(node.getRetVal());
            }

            return new int[] {alpha, beta };
        }

        return new int[] { alpha, beta };
    }

}
