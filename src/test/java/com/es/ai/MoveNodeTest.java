package com.es.ai;

import static org.junit.Assert.*;
import org.junit.Test;


public class MoveNodeTest {
    
    MoveNode root = new MoveNode();


    @Test
    public void testMoveNodeRootConstructor() {
        assertEquals(null, root.getParent());
        assertEquals(0, root.getDepth());
        assertEquals(0, root.getChildCount());
        assertEquals(1, root.getNodeCount());
    }

    @Test
    public void testAddChildDepthOne() {
        MoveNode node = root.addChild(0x63);
        
        assertNotNull(node);
        assertEquals(root, node.getParent());
        assertEquals(1, node.getDepth());
        assertEquals(0x63, node.getMove());
        assertEquals(0, node.getChildCount());
        assertEquals(1, root.getChildCount());
        assertEquals(2, root.getNodeCount());
        assertEquals(1, root.getNodeCountAtDepth(1));
    }

    @Test
    public void testAddChildDepthTwo() {
        MoveNode node1 = root.addChild(0x63);

        assertNotNull(node1);

        MoveNode node2 = node1.addChild(0x64);
        
        assertNotNull(node2);
        
        assertEquals(root, node1.getParent());
        assertEquals(node1, node2.getParent());
        
        assertEquals(1, node1.getDepth());
        assertEquals(2, node2.getDepth());
        
        assertEquals(0x63, node1.getMove());
        assertEquals(0x64, node2.getMove());
        
        assertEquals(1, node1.getChildCount());
        assertEquals(0, node2.getChildCount());
        
        assertEquals(1, root.getChildCount());
        assertEquals(3, root.getNodeCount());
        assertEquals(1, root.getNodeCountAtDepth(1));
        assertEquals(1, root.getNodeCountAtDepth(2));
    }

    @Test
    public void testSetGetScore() {
        root.setScore(23);
        
        assertEquals(23, root.getScore());
    }

    @Test
    public void testSetGetRetVal() {
        root.setRetVal(44);
        
        assertEquals(44, root.getRetVal());
    }

    @Test
    public void testGetBestWorstChild() {
        MoveNode node1 = root.addChild(0x63);
        MoveNode node2 = root.addChild(0x64);
        
        node1.setScore(25);
        node2.setScore(50);
        
        assertEquals(node2, root.getBestChild());
        assertEquals(node1, root.getWorstChild());
    }

    @Test
    public void testFindChild() {
        MoveNode node1 = root.addChild(0x63);
        MoveNode node2 = root.addChild(0x64);

        assertEquals(node1, root.findChild(0x63));
    }

    @Test
    public void testRemoveChildrenDeeperThan() {
        MoveNode node1 = root.addChild(0x63);

        assertNotNull(node1);

        MoveNode node2 = node1.addChild(0x64);
        
        assertNotNull(node2);

        int ret = root.removeChildrenDeeperThan(1);
        
        assertEquals(1, ret);
        assertEquals(2, root.getNodeCount());
        assertEquals(1, root.getNodeCountAtDepth(1));
    }

    @Test
    public void testChildrenToString() {
        String ret = root.childrenToString();
        
        assertNotNull(ret);
    }

}
