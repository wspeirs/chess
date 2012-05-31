package com.es;

import java.util.List;

public class MoveNode {
    
    private Board board;
    private int score;
    private int depth;
    private List<MoveNode> children;

    public MoveNode(Board board) {
        this.board = board;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void addChild(MoveNode node) {
        children.add(node);
    }
}
