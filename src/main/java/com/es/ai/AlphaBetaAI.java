package com.es.ai;


import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.ArraySet;
import com.es.Board;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;

public class AlphaBetaAI {
    private static final Logger LOG = LoggerFactory.getLogger(AlphaBetaAI.class);

    private final TranspositionTable transpositionTable = new TranspositionTable();
    private int transHit = 0;
    private final Color colorPlaying;
    private final Configuration configuration;
    private final int depth;

    public AlphaBetaAI(Color colorPlaying, Configuration configuration) {
        this.colorPlaying = colorPlaying;
        this.configuration = configuration;
        this.depth = configuration.getInt(CmdConfiguration.DEPTH);
    }

    public AlphaBetaAI(Color colorPlaying) {
        this.colorPlaying = colorPlaying;
        this.configuration = null;
        this.depth = 4;
    }

    public int getTransHit() {
        return transHit;
    }

    public static Color swapColor(Color color) {
        return color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public int[] computeNextMove(MoveNode node, Color color) {
        for(int d=2; d <= 4; d++) {
            transHit = 0;
            long start = System.currentTimeMillis();
            alphabeta(node, d, -1000000, 1000000, color);
            long time = System.currentTimeMillis() - start;

            System.out.println("DEPTH: " + d + " TT HITS: " + transHit + " TIME: " + time + " NODES: " + node.getNodeCount());
            System.out.println(node.childrenToString());
        }

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

        final Board board = node.getBoard();
        int[] boardPieces = board.getPieces(color);
        final int[] childrenPieces = node.getChildrenPieces();

        // we want to make sure we walk through the pieces in the same order as any children
        if(childrenPieces.length < boardPieces.length && childrenPieces.length != 0) {
            final int[] boardNoChildrenPieces = Arrays.copyOf(boardPieces, boardPieces.length);

            // remove all the children from the board
            for(int p:childrenPieces) {
                ArraySet.removeNumber(boardNoChildrenPieces, p, Board.MAX_SQUARE);
            }

            boardPieces = new int[boardPieces.length];
            int i=0;

            // copy over all of the children pieces first
            for(int childPiece:childrenPieces) {
                // copy over the child piece, only if it's still on the board
                if(Arrays.binarySearch(board.getPieces(color), childPiece) >= 0)
                    boardPieces[i++] = childPiece;
            }

            final int length = i;

            for( ; boardNoChildrenPieces[i - length] != Board.MAX_SQUARE; ++i) {
                boardPieces[i] = boardNoChildrenPieces[i - length];
            }
        }

        int[] allMoves = this.generateAllMoves(board, boardPieces);
        int[] ret = { 0, alpha, beta };

        for(int i = 0; i < allMoves.length; i += 2) {

            if(allMoves[i] == Board.MAX_SQUARE)
                break;

            MoveNode child = node.findChild(allMoves[i], allMoves[i+1]);

            if(child != null) {
                int score = alphabeta(child, depth - 1, alpha, beta, swapColor(color));

                // get the alpha or beta depending upon who's turn it is
                if(colorPlaying.equals(color)) {
                    alpha = Math.max(alpha, score);
                } else {
                    beta = Math.min(beta, score);
                }

            } else {
                // compute alpha-beta for the move
                ret = alphabeta(node, depth, allMoves[i], allMoves[i+1], alpha, beta, color);

                // update the values of alpha and beta
                alpha = ret[0];
                beta = ret[1];
            }

            if(beta <= alpha) {
                break;
            }
        }

        final int retVal = colorPlaying.equals(color) ? alpha : beta;

        int score = retVal;

        if(node.getChildCount() != 0) {
            score = (colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore();
        }

        node.setScore(score);
        node.setDepth(depth);
        node.setRetVal(retVal);

        return retVal;
    }


    public int[] alphabeta(MoveNode node, int depth, int from, int to, int alpha, int beta, Color color) {
        final Board moveBoard = new Board(node.getBoard());

        try {
            // just make sure the first move is legal
            if(depth == this.depth) {
                moveBoard.makeMove(from, to, true);
            } else {
                moveBoard.makeMove(from, to, false);
            }
        } catch (IllegalMoveException e) {
            if(!e.isKingInCheck()) {
                LOG.error("Illegal move during compute: {}", e.getMessage());
                System.out.println(moveBoard.toString());
                System.exit(-1);
            }

            // return here, but continue searching
            return new int[] { alpha, beta };
        }

        MoveNode childNode = transpositionTable.get(moveBoard);

        // check to see if we need to create a new node, or if we can use the one from the table
        if(childNode == null || childNode.getDepth() <= depth) {
            childNode = new MoveNode(moveBoard, node, new int[] { from, to });

            final int score = alphabeta(childNode, depth - 1, alpha, beta, swapColor(color));

            // get the alpha or beta depending upon who's turn it is
            if(colorPlaying.equals(color)) {
                alpha = Math.max(alpha, score);
            } else {
                beta = Math.min(beta, score);
            }

            // add the new node
            node.addChild(childNode);

            // add it to the transposition table
            transpositionTable.put(moveBoard, childNode);
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
/*
        node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
        node.setDepth(depth);
        node.setRetVal(colorPlaying.equals(color) ? alpha : beta);
*/
        return new int[] { alpha, beta };
    }

    public int[] generateAllMoves(Board board, int[] pieces) {
        int[] allMoves = new int[161 * 2];
        int i = 0;

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;  // in sorted order, so we can break early
            }

            int[] moves = board.getPiece(p).generateAllMoves(board, p);

            for(int m:moves) {
                if(m == Board.MAX_SQUARE) {
                    break;  // always in sorted order, so we're done here
                }

                allMoves[i++] = p;
                allMoves[i++] = m;
            }
        }

        Arrays.fill(allMoves, i, allMoves.length, Board.MAX_SQUARE);
        return allMoves;
    }

    public int computeScore(MoveNode node) {
        final Board board = node.getBoard();
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
        whiteScore += computeAttackDefendBonus(board, whitePieces, Color.BLACK);
        blackScore += computeAttackDefendBonus(board, blackPieces, Color.WHITE);


        if(LOG.isDebugEnabled()) {
//            LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
//            LOG.info("WHITE: {} BLACK: {} SCORE: " + (colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore), whiteScore, blackScore);
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

    public int computeAttackDefendBonus(Board board, int[] pieces, Color targetColor) {
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
                    ret += targetPiece.getValue() * .25;
                } else {
                    ret += targetPiece.getValue() * 0.50;
                }
            }
        }

        return ret;
    }

}
