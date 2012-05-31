package com.es;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;

public class MoveAI {
    private static final Logger LOG = LoggerFactory.getLogger(MoveAI.class);
    
    private MoveNode rootNode;
//    private TreeSet<MoveNode> nodes;
    private Map<Piece[], MoveNode> nodes;
    
    public MoveAI(MoveNode rootNode) {
        this.rootNode = rootNode;
    }
    
    public void computeNextMove(MoveNode currentNode, Color color) throws  CloneNotSupportedException {
        Set<Piece> pieces = rootNode.getBoard().getPieces(color);
        
        for(Piece p:pieces) {
            int[] moves = p.generateAllMoves();
            
            for(int m:moves) {
                Board moveBoard = (Board) this.clone(); // copy the board
                
                try {
                    moveBoard.makeMove(p.getCurPos(), m);
                    MoveNode moveNode = new MoveNode(moveBoard);
                    
                    MoveNode tmpNode = nodes.get(moveBoard.getBoard());
                    
                    if(tmpNode != null) {
                        currentNode.addChild(tmpNode);
                    } else {
                        currentNode.addChild(moveNode);
                    }
                    
                } catch (IllegalMoveException e) {
                    LOG.debug("Illegal move during compute: {}", e.getMessage());
                }
            }
        }
    }



}
