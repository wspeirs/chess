package com.es;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Bishop;
import com.es.pieces.King;
import com.es.pieces.Knight;
import com.es.pieces.Pawn;
import com.es.pieces.Piece.Color;
import com.es.pieces.Queen;
import com.es.pieces.Rook;

public class PgnUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PgnUtils.class);

    private Board board;

    public PgnUtils(Board board) {
        this.board = board;
    }

    public int[] parseSingleMove(Color color, String move) throws IllegalMoveException {
        int[] ret = new int[2];
        int curChar = 0;
        int pieceType = move.charAt(curChar);
        List<Integer> pieces;

        if(move.equalsIgnoreCase("O-O")) {
            return new int[] { -1, color.equals(Color.WHITE) ? 1 : 2 };
        } else if(move.equalsIgnoreCase("O-O-O")) {
            return new int[] { -2, color.equals(Color.WHITE) ? 1 : 2 };
        }

        // get a list of pieces of that type
        switch(pieceType) {
        case 'K':
            pieces = board.getPiecsOfType(color, King.class);
            curChar++;
            break;
        case 'Q':
            pieces = board.getPiecsOfType(color, Queen.class);
            curChar++;
            break;
        case 'B':
            pieces = board.getPiecsOfType(color, Bishop.class);
            curChar++;
            break;
        case 'N':
            pieces = board.getPiecsOfType(color, Knight.class);
            curChar++;
            break;
        case 'R':
            pieces = board.getPiecsOfType(color, Rook.class);
            curChar++;
            break;
        case 'P':
        default:
            pieces = board.getPiecsOfType(color, Pawn.class);
            pieceType = 'p';
        }

        // see if we have a capture
        if(move.charAt(curChar) == 'x') {
            curChar++;
        }

        int startRow = Board.MAX_ROW;
        int startCol = Board.MAX_COL;

        // check to see if we have extra info about the move
        if(move.length() - curChar > 2 && !move.endsWith("+") && !move.endsWith("#")) {
            // check to see if we have a letter
            if(move.charAt(curChar) >= 97) {
                startCol = move.charAt(curChar) - 97;
                curChar++;
            }

            if(move.charAt(curChar) <= 57) {
                startRow = move.charAt(curChar) - 49;
                curChar++;
            }

            // see if we have a capture
            if(move.charAt(curChar) == 'x') {
                curChar++;
            }
        }

        ret[1] = Board.rowColToSquare(move.charAt(curChar+1) - 49, move.charAt(curChar) - 97);
        boolean found = false;

        // go through the list and see if there is ONLY one possible piece move
        for(Integer p:pieces) {
            int row = Board.squareToRow(p);
            int col = Board.squareToCol(p);

            if(startRow != Board.MAX_ROW && startRow != row) {
                continue;
            }

            if(startCol != Board.MAX_COL && startCol != col) {
                continue;
            }

            if(Arrays.binarySearch(board.getPiece(p).generateAllMoves(board, p), ret[1]) >= 0) {
                if(found) {
                    LOG.error("Ambigious move: {}", move);
                    throw new IllegalMoveException("Ambigious move");
                }

                found = true;
                ret[0] = p;
            }
        }

        if(!found) {
            LOG.error("No piece found for the move: {}", move);
            throw new IllegalMoveException("Piece not found");
        }

        return ret;
    }

    public String computePgnMove(int move) {
        final int fromSquare = Board.getFromSquare(move);
        final int toSquare = Board.getToSquare(move);
        String ret = board.getPiece(fromSquare).toString().toUpperCase();

        ret += (char) ((fromSquare & 0x0f) + 97);
        ret += (fromSquare >> 4) + 1;

        if(board.getPiece(toSquare) != null) {
            ret += 'x';
        }

        ret += (char) ((toSquare & 0x0f) + 97);
        ret += (toSquare >> 4) + 1;

        return ret;
    }

}
