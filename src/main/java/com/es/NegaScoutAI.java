package com.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.King;
import com.es.pieces.Piece.Color;

public class NegaScoutAI {
    private static final Logger LOG = LoggerFactory.getLogger(NegaScoutAI.class);

    private Color colorPlaying;

    public NegaScoutAI(Color colorPlaying) {
        this.colorPlaying = colorPlaying;
    }

    public int negascout(MoveNode node, int depth, int alpha, int beta, Color color) {
        if(depth == 0) {
            int score = computeScore(node);
            node.setScore(score);
            node.setDepth(depth);
            return score;
        }

        Board board = node.getBoard();
        int[] pieces = board.getPieces(color);

        int b = beta;

        // generate all the moves for each of these pieces
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

                Board moveBoard = new Board(node.getBoard());

                try {
                    moveBoard.makeMove(p, m, false);
                    MoveNode childNode = new MoveNode(moveBoard, node, new int[] { p, m });

                    int score = negascout(childNode, depth - 1, -1 * b, -1 * alpha, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE) * -1;

                    if(alpha < score && score < beta && node.getChildCount() != 0) {
                        LOG.debug("NULL WINDOW FAILED HIGH");
                        score = negascout(childNode, depth - 1, -1 * beta, -1 * alpha, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE) * -1;
                    }

                    // by here we've recursed down
                    node.addChild(childNode);  // add the new node

                    alpha = Math.max(alpha, score);

                    if(alpha >= beta) {
                        LOG.debug("BETA CUTOFF");

                        if(color.equals(colorPlaying)) {
                            node.setScore(node.getBestChild().getScore());
                        } else {
                            node.setScore(node.getWorstChild().getScore());
                        }
                        node.setDepth(depth);

                        return alpha;
                    }

                    b = alpha + 1;

                } catch (IllegalMoveException e) {
                    LOG.warn("Illegal move");
                    if(!e.isKingInCheck()) {
                        LOG.error("Illegal move during compute: {}", e.getMessage());
                        moveBoard.printBoard();
                        System.exit(-1);
                    }
                }
            }
        }

        if(color.equals(colorPlaying)) {
            node.setScore(node.getBestChild().getScore());
        } else {
            node.setScore(node.getWorstChild().getScore());
        }
        node.setDepth(depth);

        LOG.debug("NORMAL ALPHA RET: {}", alpha);
        return alpha;
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
                LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
                LOG.info("SCORE: {}", colorPlaying.equals(Color.WHITE) ? (whiteScore - blackScore) * 100 : (blackScore - whiteScore) * 100);
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
            LOG.info("MOVE: {} -> {}", Integer.toHexString(node.getMove()[0]), Integer.toHexString(node.getMove()[1]));
            LOG.info("SCORE: {}", colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore);
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

}
