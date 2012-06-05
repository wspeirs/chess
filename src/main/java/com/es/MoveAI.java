package com.es;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.King;
import com.es.pieces.Piece.Color;

public class MoveAI {
    private static final Logger LOG = LoggerFactory.getLogger(MoveAI.class);

    private Map<Board, MoveNode> nodes = new HashMap<Board, MoveNode>();
    private Color colorPlaying;

    public MoveAI(Color colorPlaying) {
        this.colorPlaying = colorPlaying;
    }

    public int getTotalNodes() {
        return nodes.size();
    }

    public MoveNode findNode(Board board) {
        return nodes.get(board);
    }

    public void removeNodes(MoveNode node) {
        for(MoveNode mn:node.getChildren()) {
            // remove all children nodes
            nodes.remove(mn.getBoard());

            // remove all the children's children
            for(MoveNode cmn:mn.getChildren()) {
                nodes.remove(cmn);
            }
        }

        // remove this node
        nodes.remove(node);
    }

    public int[] computeNextMove(MoveNode currentNode, Color color) {
        computeNextChildMove(currentNode, color, 2, -1);

//        currentNode.printChildren();

//        LOG.info("NODES: {} {}", currentNode.getChildCount(), currentNode.getNodeCount());

//        List<MoveNode> removeNodes = currentNode.keepTopChildren(10);
/*
        for(MoveNode node:removeNodes) {
            this.removeNodes(node);
        }
*/
        nodes.clear();
        LOG.info("NODES: {} {}", currentNode.getChildCount(), currentNode.getNodeCount());

        MoveNode moveNode = computeNextChildMove(currentNode, color, 4, 10);
        nodes.clear();

        LOG.info("NODES: {} {}", currentNode.getChildCount(), currentNode.getNodeCount());
//        moveNode = computeNextChildMove(currentNode, color, 6, 5);

//        LOG.info("NODES: {} {}", currentNode.getChildCount(), currentNode.getNodeCount());
        return moveNode.getMove();
    }

    public MoveNode computeNextChildMove(MoveNode currentNode, Color color, int depth, int width) {
        if(depth == 0) {
            return currentNode;
        }

        if(currentNode.getChildCount() != 0) {
            final List<MoveNode> children = currentNode.getChildren();
            for(int i=0; i < (width == -1 ? children.size() : Math.min(width, children.size())); ++i) {
                final MoveNode child = children.get(i);
                if(child.getDepth() < depth) {
                    computeNextChildMove(child, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE, depth-1, width);
                }
            }

            MoveNode ret;
            if(colorPlaying.equals(color)) {
                // we made a move for ourself, so get the best
                ret = currentNode.getBestChild();
            } else {
                ret = currentNode.getWorstChild();
            }

            currentNode.setScore(ret.getScore());
            currentNode.setDepth(depth);

            return ret;   // return the best child
        } else {
//            LOG.info("CALLING NEXT: {} {}", depth, nodes.size());
            return computeNextMove(currentNode, color, depth);
        }
    }

    public MoveNode computeNextMove(MoveNode currentNode, Color color, int depth) {
        if(depth == 0) {
            // compute the score
            double score = computeScore(currentNode);

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
                    MoveNode childNode = null; // nodes.get(moveBoard);

                    if(childNode == null) {
                        childNode = new MoveNode(moveBoard, currentNode, new int[] { p, m });
                        // nodes.put(moveBoard, childNode);    // add to our set of nodes
                        computeNextMove(childNode, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE, depth - 1);
                    } else {
                        LOG.debug("GOT TRANSPOSITION");
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

    public double computeScore(MoveNode node) {
        final Board board = node.getBoard();
        final Board parentBoard = node.getParent().getBoard();
        final int[] whitePieces = board.getPieces(Color.WHITE);
        final int[] blackPieces = board.getPieces(Color.BLACK);
        final int[] whiteParentPieces = parentBoard.getPieces(Color.WHITE);
        final int[] blackParentPieces = parentBoard.getPieces(Color.BLACK);

        double whiteScore = 0.0;
        double whiteParentScore = 0.0;
        double blackScore = 0.0;
        double blackParentScore = 0.0;

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
                LOG.info("CAPTURE WHITE SCORE: {} BLACK SCORE: {}", whiteScore, blackScore);
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
            LOG.info("POSITION WHITE SCORE: {} BLACK SCORE: {}", whiteScore, blackScore);
        }

        return colorPlaying.equals(Color.WHITE) ? whiteScore - blackScore : blackScore - whiteScore;
    }

}
