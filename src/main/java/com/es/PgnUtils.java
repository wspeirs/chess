package com.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Bishop;
import com.es.pieces.King;
import com.es.pieces.Knight;
import com.es.pieces.Pawn;
import com.es.pieces.Piece;
import com.es.pieces.Piece.Color;
import com.es.pieces.Queen;
import com.es.pieces.Rook;

public class PgnUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PgnUtils.class);

    private Board board;

    public PgnUtils(Board board) {
        this.board = board;
    }

    public void parseGame(InputStream pgnStream) throws IllegalMoveException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(pgnStream));
        String line;

        try {
            while((line = reader.readLine()) != null) {
                String[] moves = StringUtils.split(line, " ");

                LOG.debug("PARSED: {} ({})", line, moves.length);

                // make white's move
                int[] move = parseSingleMove(Color.WHITE, moves[1]);
                board.makeMove(move[0], move[1]);

                // make black's move
                move = parseSingleMove(Color.BLACK, moves[2]);
                board.makeMove(move[0], move[1]);
            }
        } catch(IOException e) {
            LOG.error("Error reading pgn file: {}", e.getMessage());
        }
    }

    public int[] parseSingleMove(Color color, String move) throws IllegalMoveException {
        int[] ret = new int[2];
        int curChar = 0;
        int pieceType = move.charAt(curChar);
        List<Piece> pieces;

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
        for(Piece p:pieces) {
            int row = Board.squareToRow(p.getCurPos());
            int col = Board.squareToCol(p.getCurPos());

            if(startRow != Board.MAX_ROW && startRow != row) {
                continue;
            }

            if(startCol != Board.MAX_COL && startCol != col) {
                continue;
            }

            if(Arrays.binarySearch(p.generateAllMoves(), ret[1]) >= 0) {
                if(found) {
                    LOG.error("Ambigious move: {}", move);
                    throw new IllegalMoveException("Ambigious move");
                }

                found = true;
                ret[0] = p.getCurPos();
            }
        }

        if(!found) {
            LOG.error("No piece found for the move: {}", move);
            throw new IllegalMoveException("Piece not found");
        }

        return ret;
    }

}
