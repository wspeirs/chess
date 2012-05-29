package com.es;

import java.util.Arrays;
import java.util.List;

import com.es.pieces.Bishop;
import com.es.pieces.King;
import com.es.pieces.Knight;
import com.es.pieces.Pawn;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;
import com.es.pieces.Queen;
import com.es.pieces.Rook;

public class PgnUtils {
    
    private Board board;
    
    public PgnUtils(Board board) {
        this.board = board;
    }
    
    public int[] parseSingleMove(Color color, String move) throws IllegalMoveException {
        int[] ret = new int[2];
        int curChar = 0;
        int pieceType = move.charAt(curChar);
        List<Piece> pieces;
        
        // get a list of pieces of that type
        switch(pieceType) {
        case 'K':
        case 'k':
            pieces = board.getPiecsOfType(color, King.class);
            curChar++;
            break;
        case 'Q':
        case 'q':
            pieces = board.getPiecsOfType(color, Queen.class);
            curChar++;
            break;
        case 'B':
        case 'b':
            pieces = board.getPiecsOfType(color, Bishop.class);
            curChar++;
            break;
        case 'N':
        case 'n':
            pieces = board.getPiecsOfType(color, Knight.class);
            curChar++;
            break;
        case 'R':
        case 'r':
            pieces = board.getPiecsOfType(color, Rook.class);
            curChar++;
            break;
        default:
            pieces = board.getPiecsOfType(color, Pawn.class);
            pieceType = 'p';
        }
        
        // see if we have a capture
        if(move.charAt(curChar) == 'x') {
            curChar++;
        }
        
        ret[1] = Board.rowColToSquare(move.charAt(curChar+1) - 49, move.charAt(curChar) - 97);
        boolean found = false;
        
        // go through the list and see if there is ONLY one possible piece move
        for(Piece p:pieces) {
            if(Arrays.binarySearch(p.generateAllMoves(), ret[1]) >= 0) {
                if(found) {
                    throw new IllegalMoveException("Ambigious move");
                }
                
                found = true;
                ret[0] = p.getCurPos();
            }
        }
        
        if(!found) {
            throw new IllegalMoveException("Piece not found");
        }
        
        return ret;
    }
    
}
