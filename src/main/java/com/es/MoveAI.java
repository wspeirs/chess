package com.es;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.King;
import com.es.pieces.Piece.Color;

public class MoveAI {
    private static final Logger LOG = LoggerFactory.getLogger(MoveAI.class);

    private TranspositionTable transpositionTable = new TranspositionTable();
    private int transHit = 0;
    private Color colorPlaying;

    public MoveAI(Color colorPlaying) {
        this.colorPlaying = colorPlaying;
    }

    public MoveNode findNode(Board board) {
        return transpositionTable.get(board);
    }

/*
    public void removeNodes(MoveNode node) {
        for(MoveNode mn:node.getChildren()) {
            // remove all children transpositionTable
            transpositionTable.remove(mn.getBoard());

            // remove all the children's children
            for(MoveNode cmn:mn.getChildren()) {
                transpositionTable.remove(cmn);
            }
        }

        // remove this node
        transpositionTable.remove(node);
    }
*/

    public int[] computeNextMove(MoveNode currentNode, Color color) {
        transHit = 0;   // reset our trans table counter

        //
        // We start with a depth of 2 and not restrictions on the width
        //
        MoveNode moveNode = computeNextChildMove(currentNode, color, 2, 2);
        LOG.info("NODE STATS: {}", new int[] { currentNode.getDepth(), currentNode.getChildCount(), transHit, transpositionTable.size(), currentNode.getNodeCount() });


        // After going down 2, keep only the top 10 moves
//        currentNode.keepTopChildren(10);

        for(int i=3; i < 5; ++i) {
            transHit = 0;   // reset our trans table counter
            moveNode = computeNextChildMove(currentNode, color, i, i);
            LOG.info("NODE STATS: {}", new int[] { currentNode.getDepth(), currentNode.getChildCount(), transHit, transpositionTable.size(), currentNode.getNodeCount() });
        }

        return moveNode.getMove();
    }

    public MoveNode computeNextChildMove(MoveNode currentNode, Color color, int depth, int height) {
        if(depth == 0) {
//            LOG.warn("HIT BOTTOM - SHOULDN'T BE HERE");
            return currentNode;
        }

        MoveNode ret;

        // we have children, so we've searched this level already, so keep moving down
        if(currentNode.getChildCount() != 0) {
            final List<MoveNode> children = currentNode.getChildren();

            int width = children.size();

            if(height > 3) {
//                width = color.equals(colorPlaying) ? 1 : 2;
                width = 1;
            }

            for(int i=0; i < (width == -1 ? children.size() : Math.min(width, children.size())); ++i) {
                final MoveNode child = children.get(i);
                if(child.getDepth() < depth) {
                    computeNextChildMove(child, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE, depth-1, height);
                }
            }

            ret = (colorPlaying.equals(color) ? currentNode.getBestChild() : currentNode.getWorstChild());

            currentNode.setScore(ret.getScore());
            currentNode.setDepth(depth);

        } else {
            ret = computeNextMove(currentNode, color, depth);
        }

        return ret;
    }

    public MoveNode computeNextMove(MoveNode currentNode, Color color, int depth) {
        if(depth == 0) {
            // compute the score
            int score = computeScore(currentNode);

            currentNode.setScore(score);

            // set the depth
            currentNode.setDepth(0);
            return currentNode;
        }

        Board board = currentNode.getBoard();
        int[] pieces = board.getPieces(color);

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

                Board moveBoard = new Board(currentNode.getBoard());

                try {
                    //LOG.debug("Move: {} -> {}", Integer.toHexString(p), Integer.toHexString(m));

                    moveBoard.makeMove(p, m, false);
                    MoveNode childNode = null; // transpositionTable.get(moveBoard);

                    if(childNode == null) {
                        childNode = new MoveNode(moveBoard, currentNode, new int[] { p, m });
                        transpositionTable.put(moveBoard, childNode);    // add to our transposition table
                        computeNextMove(childNode, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE, depth - 1);
                    } else {
                        transHit++;
                    }

                    // by here we've recursed down
                    currentNode.addChild(childNode);  // add the new node

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

        // all moves put the king into check
        if(currentNode.getChildCount() == 0) {
            currentNode.getBoard().printBoard();
            currentNode.setScore(computeScore(currentNode));
            currentNode.setDepth(depth);
            return currentNode;
        }

        // we've added all the children, so get the score
        MoveNode child;

        if(colorPlaying.equals(color)) {
            // we made a move for ourself, so get the best
            child = currentNode.getBestChild();
        } else {
            child = currentNode.getWorstChild();
        }

        currentNode.setScore(child.getScore());
        currentNode.setDepth(depth);

        return child;   // return the best child
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
            LOG.info("WHITE: {} BLACK: {}", whiteScore, blackScore);
            LOG.info("SCORE: {}", colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore);
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

}
