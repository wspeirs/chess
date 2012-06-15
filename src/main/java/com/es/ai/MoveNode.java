package com.es.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.PgnUtils;

public final class MoveNode {

    private static final Logger LOG = LoggerFactory.getLogger(MoveNode.class);

    private static final String LINE_BREAK = System.getProperty("line.separator");

    private static final MoveNodeComparitor increasingComparitor = new MoveNodeComparitor(true);
    private static final MoveNodeComparitor decreasingComparitor = new MoveNodeComparitor(false);

    private final Board board;
    private final int[] move;
    private int score;
    private int retVal;
    private int depth;
    private MoveNode parent;
    private List<MoveNode> children = new ArrayList<MoveNode>();
    private boolean isSorted = false;

    public MoveNode(Board board, MoveNode parent, int[] move) {
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

    public int[] getMove() {
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
    
    public MoveNode findChild(int from, int to) {
        for(MoveNode child:children) {
            int[] move = child.getMove();
            
            if(move[0] == from && move[1] == to) {
                return child;
            }
        }
        
        return null;
    }
    
    public int[] getChildrenPieces() {
        int[] ret = new int[children.size()];
        int i =0;
        
        for(MoveNode child:children) {
            ret[i++] = child.getMove()[0];
        }
        
        return ret;
    }

    public String childrenToString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<MoveNode> it = children.iterator();

        while(it.hasNext()) {
            MoveNode curNode = it.next();
            int[] move = curNode.getMove();

            sb.append(curNode.getScore());
            sb.append(": ");
            
            sb.append(new PgnUtils(curNode.parent.board).computePgnMove(move[0], move[1]));
            
            while(curNode.getChildCount() != 0) {
                curNode = curNode.getFirstChild();
                move = curNode.getMove();
                sb.append(" ");
                sb.append(new PgnUtils(curNode.parent.board).computePgnMove(move[0], move[1]));
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
