package com.es.ai;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.IllegalMoveException;
import com.es.pieces.King;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;

public class WorkingAlphaBetaAI {
    private static final Logger LOG = LoggerFactory.getLogger(AlphaBetaAI.class);

    private TranspositionTable transpositionTable = new TranspositionTable();
    private int transHit = 0;
    private Color colorPlaying;
    private final Board board;

    public WorkingAlphaBetaAI(Color colorPlaying, Board board) {
        this.colorPlaying = colorPlaying;
        this.board = board;
    }
    
    public int getTransHit() {
        return transHit;
    }

    public int computeNextMove(MoveNode node, Color color) {
        alphabeta(node, 6, -1000000, 1000000, color);

        return node.getBestChild().getMove();
    }

    public int alphabeta(MoveNode node, int depth, int alpha, int beta, Color color) {
        if(depth == 0) {
            int score = computeScore();
            node.setScore(score);
            node.setDepth(depth);
            node.setRetVal(score);
            return score;
        }

        int[] pieces = board.getPieces(color);

        for(int p:pieces) {
            if(p == Board.MAX_SQUARE) {
                break;
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

                try {
                    board.makeMove(Board.createMoveValue(p, m, '-'));
                    MoveNode childNode = transpositionTable.get(board);
                    boolean addToTable = false;
                    if(childNode == null || childNode.getDepth() <= depth) {
                        childNode = new MoveNode(node, Board.createMoveValue(p, m, '-'));
                        addToTable = true;
                        if(colorPlaying.equals(color)) {
                            alpha = Math.max(alpha, alphabeta(childNode, depth - 1, alpha, beta, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE));
                        } else {
                            beta = Math.min(beta, alphabeta(childNode, depth - 1, alpha, beta, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE));
                        }
                    } else {
                        if( (color.equals(Color.WHITE) && p > 0x30) || (color.equals(Color.BLACK) && p < 0x40)) {
                            LOG.debug("TRANS HIT: {}", color.equals(Color.WHITE) ? "WHITE" : "BLACK");
                            LOG.debug("Move: {} -> {}", Integer.toHexString(p), Integer.toHexString(m));
                        }

                        if(colorPlaying.equals(color)) {
                            alpha = Math.max(alpha, childNode.getRetVal());
                        } else {
                            beta = Math.min(beta, childNode.getRetVal());
                        }

                        transHit++;
                    }

                    if(addToTable) {
                        transpositionTable.put(board, childNode);
                    }

                    // by here we've recursed down
                    node.addChild(childNode);  // add the new node

                    if(beta <= alpha) {
                        //LOG.debug("{} <= {}; RETURNING ALPHA", beta, alpha);
                        node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
                        node.setDepth(depth);
                        int retVal = colorPlaying.equals(color) ? alpha : beta;
                        node.setRetVal(retVal);
                        return retVal;
                    }

                } catch (IllegalMoveException e) {
                    LOG.warn("Illegal move");
                    if(!e.isKingInCheck()) {
                        LOG.error("Illegal move during compute: {}", e.getMessage());
                        System.out.println(board.toString());
                        System.exit(-1);
                    }
                }
            }
        }

        node.setScore((colorPlaying.equals(color) ? node.getBestChild() : node.getWorstChild()).getScore());
        node.setDepth(depth);

        int retVal = colorPlaying.equals(color) ? alpha : beta;
        node.setRetVal(retVal);

        //LOG.debug("NORMAL ALPHA RET: {}", alpha);
        return retVal;
    }

    public int computeScore() {
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