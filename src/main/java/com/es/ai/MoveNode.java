package com.es.ai;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;

/**
 * Represents a node in the move tree.
 *
 * The depth of the root node is zero with children being a depth 1, etc...
 */
public final class MoveNode {

    private static final Logger LOG = LoggerFactory.getLogger(MoveNode.class);

    private static final String LINE_BREAK = System.getProperty("line.separator");

    private static final MoveNodeComparitor increasingComparitor = new MoveNodeComparitor(true);
    //private static final MoveNodeComparitor decreasingComparitor = new MoveNodeComparitor(false);

    private final WeakReference<MoveNode> parent;
    private final int move;
    private final int depth;
    private final List<MoveNode> children = new ArrayList<MoveNode>();
    private int score;
    private int retVal;
    private boolean isSorted = false;

    /**
     * Constructs the root node of a MoveNode tree.
     */
    public MoveNode() {
        this.move = 0;
        this.parent = null;
        this.depth = 0;
    }

    /**
     * Private constructor for creating children nodes.
     * @param parent the parent of this node.
     * @param move the move for this node.
     * @param depth the depth of this node in the tree.
     */
    private MoveNode(MoveNode parent, int move, int depth) {
        this.parent = new WeakReference<MoveNode>(parent);
        this.move = move;
        this.depth = depth;
    }

    /**
     * Adds a child to this MoveNode by constructing it and linking it in.
     * @param move the move for the node.
     * @return the newly created MoveNode.
     */
    public MoveNode addChild(int move) {
        final MoveNode ret = new MoveNode(this, move, this.depth + 1);

        children.add(ret);
        isSorted = false;

        return ret;
    }

    public MoveNode getParent() {
        return parent == null ? null : parent.get();
    }

    public List<MoveNode> getChildren() {
        return children;
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

    public int getMove() {
        return move;
    }

    public int getChildCount() {
        return children.size();
    }

    public MoveNode getFirstChild() {
        return children.get(0);
    }

    /**
     * Gets the best child of this node.
     *
     * It does so by sorting the children by score in increasing order, then returning the first child.
     * @return the MoveNode with the highest score child.
     */
    public MoveNode getBestChild() {
        if(!isSorted) {
            Collections.sort(children, increasingComparitor);
        }

        return children.get(0);
    }

    /**
     * Gets the worst child of this node.
     *
     * It does so by sorting the children by score in decreasing order, then returning the first child.
     * @return the MoveNode with the lowest score child.
     */
    public MoveNode getWorstChild() {
        if(!isSorted) {
            Collections.sort(children, increasingComparitor);
        }

        return children.get(children.size()-1);
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

    /**
     * Internal function to add a node to this node's children.
     * @param node the node to add to this node's children.
     */
    private void addChild(MoveNode node) {
        this.children.add(node);
    }

    /**
     * Given a move, find the child associated with this move.
     * @param move the move to find.
     * @return the MoveNode associated with this move, or null if not found.
     */
    public MoveNode findChild(int move) {
        for(MoveNode child:children) {
            int m = child.getMove();

            if(move == m) {
                return child;
            }
        }

        return null;
    }

    /**
     * Removes all of the children from the node running the garbage collector.
     * @return the number of children removed.
     */
    public int clearChildren() {
        return clearChildren(true);
    }

    /**
     * Removes all of the children from the node.
     * @param runGC true = run garbage collector.
     * @return the number of children removed.
     */
    public int clearChildren(boolean runGC) {
        final int ret = children.size();

        if(ret != 0) {
            for(MoveNode child:children) {
                child.clearChildren(false);
            }

            children.clear();

            if(runGC)
                System.gc(); // run the GC to reclaim memory
        }

        return ret;
    }

/*
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
*/

    /**
     * Removes children that are deeper than the provided depth.
     * @param depth the cutoff depth.
     * @return the number of children removed.
     */
    public int removeChildrenDeeperThan(int depth) {
        final int ret = removeChildrenDeeperThan(0, depth);

        // if we removed children, run the GC to reclaim the space
        if(ret != 0)
            System.gc();

        return ret;
    }

    private int removeChildrenDeeperThan(int removeCount, int depth) {
        final Iterator<MoveNode> it = children.iterator();

        while(it.hasNext()) {
            final MoveNode node = it.next();

            if(node.depth > depth) {
                // then remove this node
                it.remove();
                ++removeCount;
            } else {
                // recurse
                removeCount = node.removeChildrenDeeperThan(depth);
            }
        }

        return removeCount;
    }

    /**
     * Makes this node a root.
     *
     * This works by going to the parent and removing all children.
     */
    public void makeRootNode() {
        if(this.parent == null)
            return; // we're already a root node

        final MoveNode parentNode = this.parent.get();

        if(parentNode == null)
            return; // we're already a root node

        // go through the children, skipping this one clearing them
        for(MoveNode child:parentNode.children) {
            if(child == this)
                continue;

            // clear the children, but don't run the GC we'll save that for the end
            child.clearChildren(false);
        }

        System.gc(); // run the GC to reclaim memory
    }

    public String childrenToString() {
        final StringBuilder sb = new StringBuilder();

        // sort the children
        Collections.sort(children, increasingComparitor);

        final Iterator<MoveNode> it = children.iterator();

        while(it.hasNext()) {
            MoveNode curNode = it.next();
            int move = curNode.getMove();

            sb.append(curNode.getScore());
            sb.append(") ");

            sb.append(curNode.depth);
            sb.append(": ");
            sb.append(Board.moveToString(move));

            while(curNode.getChildCount() != 0) {
                curNode = curNode.getBestChild();
                move = curNode.getMove();
                sb.append(" ");

                if(curNode == null || curNode.parent == null) {
                    continue;
                }

                sb.append(curNode.depth);
                sb.append(": ");
                sb.append(Board.moveToString(move));
            }

            sb.append(LINE_BREAK);
        }

        return sb.toString();
    }

    /**
     * Returns the total number of nodes in the tree.
     * @return the total number of nodes in the tree.
     */
    public int getNodeCount() {
        return getNodeCount(0, this.depth);
    }

    private int getNodeCount(int count, int depth) {
        for(MoveNode c:children) {
            // only recurse if it's not a transposition
            if(c.depth < depth) {
                continue;
            }

            count = c.getNodeCount(count, depth+1);
        }

        return count + 1;
    }

    /**
     * Counts all of the nodes in the tree at the depth specified.
     * @param depth the depth to search.
     * @return the number of nodes in the tree at the specified depth.
     */
    public int getNodeCountAtDepth(int depth) {
        return getNodeCountAtDepth(0, depth);
    }

    private int getNodeCountAtDepth(int count, int depth) {
        // if this node is deeper than the depth, then short-circuit and return
        if(this.depth > depth) {
            return count;
        }

        for(MoveNode child:children) {
            if(child.depth == depth) {
                count++;
            }

            count = child.getNodeCountAtDepth(count, depth);
        }

        return count;
    }

    /**
     * A MoveNode Comparator that compares based upon score.
     */
    private static class MoveNodeComparitor implements Comparator<MoveNode> {

        private boolean increasing;

        public MoveNodeComparitor(boolean increasing) {
            this.increasing = increasing;
        }

        @Override
        public int compare(MoveNode node1, MoveNode node2) {
            if(increasing) {
                return (node2.score < node1.score) ? -1 : ((node2.score > node1.score) ? 1 : 0);
            } else {
                return (node1.score < node2.score) ? -1 : ((node1.score > node2.score) ? 1 : 0);
            }
        }
    }
}
