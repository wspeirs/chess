package com.es.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.ArraySet;
import com.es.Board;
import com.es.PgnUtils;
import com.es.pieces.Piece.Color;

public final class MoveNode {

    private static final Logger LOG = LoggerFactory.getLogger(MoveNode.class);

    private static final String LINE_BREAK = System.getProperty("line.separator");

    private static final MoveNodeComparitor increasingComparitor = new MoveNodeComparitor(true);
    private static final MoveNodeComparitor decreasingComparitor = new MoveNodeComparitor(false);

    private final Board board;
    private final int move;
    private int score;
    private int retVal;
    private int depth;
    private MoveNode parent;
    private List<MoveNode> children = new ArrayList<MoveNode>();
    private boolean isSorted = false;

    public MoveNode(Board board, MoveNode parent, int move) {
        this.board = board;
        this.move = move;
        this.parent = parent;
    }

    public MoveNode getParent() {
        return parent;
    }

    public Board getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRetVal() {
        return retVal;
    }

    public void setRetVal(int retVal) {
        this.retVal = retVal;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMove() {
        return move;
    }

    public int getChildCount() {
        return children.size();
    }

    public MoveNode getFirstChild() {
        return children.get(0);
    }

    public void clearChildren() {
        for(MoveNode child:children) {
            child.parent = null;
            // child.clearChildren();
        }

        children.clear();
    }

    public MoveNode getBestChild() {
        if(!isSorted) {
            Collections.sort(children, increasingComparitor);
        }

        return children.get(0);
    }

    public MoveNode getWorstChild() {
        if(!isSorted) {
            Collections.sort(children, decreasingComparitor);
        }

        return children.get(0);
    }

    public void addChild(MoveNode node) {
        isSorted = false;
        children.add(node);
    }

    /**
     * Adds all of the node's children to this node's children.
     * @param node The node who's children should be added
     */
    public void addChildren(MoveNode node) {
        for(MoveNode child:node.children) {
            this.addChild(child);
        }
    }

    public MoveNode findChild(int move) {
        for(MoveNode child:children) {
            int m = child.getMove();

            if(move == m) {
                return child;
            }
        }

        return null;
    }
    
    public int removeChildrenAfter(MoveNode child) {
        if(child == null) {
            return 0;
        }
        
        Iterator<MoveNode> it = children.iterator();
        boolean found = false;
        int removeCount = 0;
        
        while(it.hasNext()) {
            MoveNode node = it.next();
            
            if(node.equals(child)) {
                found = true;
                continue;
            }
            
            if(found) {
                node.clearChildren();
                it.remove();
                ++removeCount;
            }
        }
        
        return removeCount;
    }

    public int removeNotAtDepth(int depth) {
        Iterator<MoveNode> it = children.iterator();
        int removeCount = 0;
        
        while(it.hasNext()) {
            MoveNode node = it.next();
            
            if(node.depth != depth) {
                node.clearChildren();
                it.remove();
                ++removeCount;
            }
        }
        
        return removeCount;
    }

    public int[] getChildrenPieces() {
        final int[] ret = new int[children.size()];
        final int[] set = new int[children.size()];

        if(ret.length == 0) {
            return ret;
        }

        Arrays.fill(set, Board.MAX_SQUARE);

        int i = 0;
        for(final MoveNode child:children) {
            final int piece = Board.getFromSquare(child.getMove());

            // add the piece to ret only if we haven't already added it
            if(ArraySet.addNumber(set, piece)) {
                ret[i++] = piece;
            }
        }
        // return the array with the unique pieces
        return Arrays.copyOf(ret, i);
    }

    public String childrenToString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<MoveNode> it = children.iterator();
        final AlphaBetaAI ai = new AlphaBetaAI(Color.BLACK);

        while(it.hasNext()) {
            MoveNode curNode = it.next();
            int move = curNode.getMove();

            sb.append(curNode.getScore());
            sb.append(": ");

            sb.append(new PgnUtils(curNode.parent.board).computePgnMove(move));

            sb.append(" (");
            sb.append(ai.computeScore(curNode));
            sb.append(" ");
            sb.append(curNode.depth);
            sb.append(")");

            while(curNode.getChildCount() != 0) {
                curNode = curNode.getFirstChild();
                move = curNode.getMove();
                sb.append(" ");
                if(curNode == null || curNode.parent == null) {
                    continue;
                }
                sb.append(new PgnUtils(curNode.parent.board).computePgnMove(move));

                sb.append(" (");
                sb.append(ai.computeScore(curNode));
                sb.append(" ");
                sb.append(curNode.depth);
                sb.append(")");
            }

            sb.append(LINE_BREAK);
        }

        return sb.toString();
    }

    public int getNodeCount() {
        return getNodeCount(0, this.depth);
    }

    private int getNodeCount(int count, int depth) {
        for(MoveNode c:children) {
            // only recurse if it's not a transposition
            if(c.depth > depth) {
                continue;
            }

            count = c.getNodeCount(count, depth-1);
        }

        return count + 1;
    }

    private static class MoveNodeComparitor implements Comparator<MoveNode> {

        private boolean increasing;

        public MoveNodeComparitor(boolean increasing) {
            this.increasing = increasing;
        }

        public int compare(MoveNode node1, MoveNode node2) {
            if(increasing) {
                return (node2.score < node1.score) ? -1 : ((node2.score > node1.score) ? 1 : 0);
            } else {
                return (node1.score < node2.score) ? -1 : ((node1.score > node2.score) ? 1 : 0);
            }
        }
    }
}
