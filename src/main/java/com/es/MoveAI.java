package com.es;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;

public class MoveAI {
    private static final Logger LOG = LoggerFactory.getLogger(MoveAI.class);

    private Map<Piece[], MoveNode> nodes = new HashMap<Piece[], MoveNode>();
    
    public void computeNextMove(MoveNode currentNode, Color color) throws CloneNotSupportedException {
        computeNextMove(currentNode, color, 2);
    }
    
    public void computeNextMove(MoveNode currentNode, Color color, int depth) throws CloneNotSupportedException {
        if(depth == 0) {
            return;
        }
        
        Set<Piece> pieces = currentNode.getBoard().getPieces(color);
        
        // generate all the moves for each of these pieces
        for(Piece p:pieces) {
            int[] moves = p.generateAllMoves(currentNode.getBoard());
            
            for(int m:moves) {
                if(m == Board.MAX_SQUARE) {
                    continue;
                }
                
                Board moveBoard = new Board(currentNode.getBoard());
                
                try {
                    LOG.debug("Move: {} -> {}", Integer.toHexString(p.getCurPos()), Integer.toHexString(m));
                    
                    moveBoard.makeMove(p.getCurPos(), m);
                    MoveNode childNode = nodes.get(moveBoard.getBoard());
                    
                    if(childNode == null) {
                        childNode = new MoveNode(moveBoard);
                        currentNode.addChild(childNode);  // add the new node
                        computeNextMove(childNode, color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE, --depth);
                    } else {
                        currentNode.addChild(childNode);  // transposition, so no need to search
                    }
                    
                } catch (IllegalMoveException e) {
                    LOG.debug("Illegal move during compute: {}", e.getMessage());
                    moveBoard.printBoard();
                    System.exit(-1);
                }
            }
        }
    }



}
