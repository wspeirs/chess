package com.es.ai;


import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.pieces.King;
import com.es.pieces.Piece.Color;

public class AlphaBetaAI {
    private static final Logger LOG = LoggerFactory.getLogger(AlphaBetaAI.class);

    private TranspositionTable transpositionTable = new TranspositionTable();
    private int transHit = 0;
    private Color colorPlaying;
    private Configuration configuration;

    public AlphaBetaAI(Color colorPlaying, Configuration configuration) {
        this.colorPlaying = colorPlaying;
        this.configuration = configuration;
    }

    public int getTransHit() {
        return transHit;
    }

    public int[] computeNextMove(MoveNode node, Color color) {
        alphabeta(node, configuration.getInt(CmdConfiguration.DEPTH), -1000000, 1000000, color);

        return node.getBestChild().getMove();
    }

    public int alphabeta(MoveNode node, int depth, int alpha, int beta, Color color) {
        if(depth == 0) {
            int score = computeScore(node);
            node.setScore(score);
            node.setDepth(depth);
            node.setRetVal(score);
            return score;
        }

        Board board = node.getBoard();
        int[] boardPieces = board.getPieces(color);

/*
        int[] childrenPieces = node.getChildrenPieces();
        int[] pieces = childrenPieces;

        // we want to make sure we walk through all the pieces
        if(childrenPieces.length < boardPieces.length) {
            int[] boardNoChildrenPieces = Arrays.copyOf(boardPieces, boardPieces.length);

            for(int p:childrenPieces) {
                ArraySet.removeNumber(boardNoChildrenPieces, p, Board.MAX_SQUARE);
            }
        }
*/
        int[] allMoves = this.generateAllMoves(board, boardPieces);
        int[] ret = { 0, -100000000 };

        for(int i = 0; i < allMoves.length; i += 2) {

            if(allMoves[i] == Board.MAX_SQUARE)
                break;

            // compute alpha-beta for the move
            ret = alphabeta(node, depth, allMoves[i], allMoves[i+1], alpha, beta, color);

            // update the values of alpha and beta
            alpha = ret[1];
            beta = ret[2];

            // we need to break from this loop
            if(ret[0] == -1) {
                break;
            }
        }

        final int retVal = colorPlaying.equals(color) ? alpha : beta;

        node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
        node.setDepth(depth);
        node.setRetVal(retVal);

        return retVal;
    }


    public int[] alphabeta(MoveNode node, int depth, int from, int to, int alpha, int beta, Color color) {
        final Board moveBoard = new Board(node.getBoard());

        try {
            moveBoard.makeMove(from, to, false);
        } catch (IllegalMoveException e) {
            LOG.warn("Illegal move");
            if(!e.isKingInCheck()) {
                LOG.error("Illegal move during compute: {}", e.getMessage());
                System.out.println(moveBoard.toString());
                System.exit(-1);
            }

            LOG.info("GOT HERE!!!");
            // return here, but continue searching
            return new int[] { 1, alpha, beta };
        }

        MoveNode childNode = transpositionTable.get(moveBoard);

        // check to see if we need to create a new node, or if we can use the one from the table
        if(childNode == null || childNode.getDepth() <= depth) {
            childNode = new MoveNode(moveBoard, node, new int[] { from, to });

            // get the alpha or beta depending upon who's turn it is
            if(colorPlaying.equals(color)) {
                alpha = Math.max(alpha, alphabeta(childNode, depth - 1, alpha, beta, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE));
            } else {
                beta = Math.min(beta, alphabeta(childNode, depth - 1, alpha, beta, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE));
            }

            // add the new node
            node.addChild(childNode);

            // add it to the transposition table
            transpositionTable.put(moveBoard, childNode);
        } else {
            // using the node from the table, get the alpha and beta
            if(colorPlaying.equals(color)) {
                alpha = Math.max(alpha, childNode.getRetVal());
            } else {
                beta = Math.min(beta, childNode.getRetVal());
            }

            transHit++;

            // add the child node's children to the current node's children
            node.addChildren(childNode);
        }

        // see if we have a cut-off
        if(beta <= alpha) {
            node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
            node.setDepth(depth);
            node.setRetVal(colorPlaying.equals(color) ? alpha : beta);

            return new int[] {-1, alpha, beta };
        }


        return new int[] { 1, alpha, beta };
    }

    public int[] generateAllMoves(Board board, int[] pieces) {
//        ArrayIntList allMoves = new ArrayIntList(161);
        int[] allMoves = new int[161 * 2];
        int i = 0;

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;  // in sorted order, so we can break early
            }

            // only check the king moves if it's in check
            if(board.getPiece(p) instanceof King && !board.isInCheck(p)) {
                continue;
            }

            int[] moves = board.getPiece(p).generateAllMoves(board, p);

            for(int m:moves) {
                if(m == Board.MAX_SQUARE) {
                    break;  // always in sorted order, so we're done here
                }

                allMoves[i++] = p;
                allMoves[i++] = m;

//                allMoves.add(p);
//                allMoves.add(m);
            }
        }

        Arrays.fill(allMoves, i, allMoves.length, Board.MAX_SQUARE);
        return allMoves;

//        return allMoves.toArray();

    }

    public int computeScore(MoveNode node) {
        final Board board = node.getBoard();
        final Board parentBoard = node.getParent().getBoard();
        final int[] whitePieces = board.getPieces(Color.WHITE);
        final int[] blackPieces = board.getPieces(Color.BLACK);
        final int[] whiteParentPieces = parentBoard.getPieces(Color.WHITE);
        final int[] blackParentPieces = parentBoard.getPieces(Color.BLACK);

        int whiteScore = 0;
        int whiteParentScore = 0;
        int blackScore = 0;
        int blackParentScore = 0;

        for(int i=0; i < whitePieces.length; ++i) {
            if(whitePieces[i] != Board.MAX_SQUARE) {
                whiteScore += board.getPiece(whitePieces[i]).getValue();
            }

            if(whiteParentPieces[i] != Board.MAX_SQUARE) {
                whiteParentScore += parentBoard.getPiece(whiteParentPieces[i]).getValue();
            }

            if(blackPieces[i] != Board.MAX_SQUARE) {
                blackScore += board.getPiece(blackPieces[i]).getValue();
            }

            if(blackParentPieces[i] != Board.MAX_SQUARE) {
                blackParentScore += parentBoard.getPiece(blackParentPieces[i]).getValue();
            }
        }

        // check to see if we've lost a pieces between the parent move and this move
        if(whiteScore != whiteParentScore || blackScore != blackParentScore) {
            if(LOG.isDebugEnabled()) {
//                LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
//                LOG.info("SCORE: {}", colorPlaying.equals(Color.WHITE) ? (whiteScore - blackScore) * 100 : (blackScore - whiteScore) * 100);
            }
            return colorPlaying.equals(Color.WHITE) ? (whiteScore - blackScore) * 100 : (blackScore - whiteScore) * 100;
        }

        whiteScore = 0;
        blackScore = 0;

        // compute the value based upon position
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

        if(LOG.isDebugEnabled()) {
//            LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
//            LOG.info("WHITE: {} BLACK: {} SCORE: " + (colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore), whiteScore, blackScore);
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

}
