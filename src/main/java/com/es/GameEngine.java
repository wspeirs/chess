package com.es;

import com.es.pieces.Piece.Color;

public class GameEngine {
    
    public static void main(String[] args) throws Exception {
        Board board = new Board();  // setup the board
        MoveNode currentNode = new MoveNode(board);
        MoveAI ai = new MoveAI();    // create the AI
        
        ai.computeNextMove(currentNode, Color.WHITE);
    }

}
