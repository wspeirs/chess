package com.es;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MoveNode {

    private Board board;
    private double score;
    private int depth;
    private int[] move;
    private List<MoveNode> children = new ArrayList<MoveNode>();
    private int nodeCount = 0;
    private boolean isSorted = false;

    public MoveNode(Board board, int[] move) {
        this.board = board;
        this.move = move;
    }

    public Board getBoard() {
        return board;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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

    public List<MoveNode> getChildren() {
        return children;
    }

    public List<MoveNode> keepTopChildren(int numberToKeep) {
        List<MoveNode> bottomChildren = new ArrayList<MoveNode>();
        Iterator<MoveNode> it = children.iterator();

        for(int i=0; it.hasNext(); ++i) {
            if(i < numberToKeep) {
                it.next();
            } else {
                MoveNode tmp = it.next();
                bottomChildren.add(tmp);
                it.remove();
            }
        }

        return bottomChildren;
    }

    public MoveNode getBestChild() {
        if(!isSorted) {
            Collections.sort(children, new MoveNodeComparitor());
        }

        return children.get(0);
    }

    public MoveNode getWorstChild() {
        if(!isSorted) {
            Collections.sort(children, new MoveNodeComparitor());
        }

        return children.get(children.size()-1);
    }

    public void addChild(MoveNode node) {
        isSorted = false;
        children.add(node);
    }

    public void printChildren() {
        Iterator<MoveNode> it = children.iterator();

        while(it.hasNext()) {
            MoveNode curNode = it.next();
            int[] move = curNode.getMove();

            System.out.println(curNode.getScore() + ": " + Integer.toHexString(move[0]) + " -> " + Integer.toHexString(move[1]));
        }
    }

    public int getNodeCount() {
        for(MoveNode c:children) {
            nodeCount += c.getNodeCount();
        }

        nodeCount += children.size();

        return nodeCount;
    }

    private static class MoveNodeComparitor implements Comparator<MoveNode> {

        public int compare(MoveNode node1, MoveNode node2) {
            return (node2.score < node1.score) ? -1 : ((node2.score > node1.score) ? 1 : 0);
        }
    }
}
