package com.es.engines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.IllegalMoveException;
import com.es.PgnUtils;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;
import com.es.pieces.Piece.Color;

public class GuiEngine implements Engine {

    private static final Logger LOG = LoggerFactory.getLogger(GuiEngine.class);

    private Configuration config;
    private Board board;
    private AlphaBetaAI ai;
    private PgnUtils utils;

    public GuiEngine(Configuration config) {
        this.config = config;
        this.board = new Board();
        this.ai = new AlphaBetaAI(Color.BLACK);
        this.utils = new PgnUtils(this.board);
    }

    @Override
    public void play() {
        MoveNode currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "go";

        System.out.println(board.toString());

        while(!"q".equalsIgnoreCase(line)) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                LOG.error("Error reading input: {}", e.getMessage());
                break;
            }

            int[] userMove;
            try {
                userMove = utils.parseSingleMove(Color.WHITE, line);

                board.makeMove(userMove[0], userMove[1]);
            } catch(IllegalMoveException e) {
                System.err.println("Illegal user move: " + e.getMessage());
                continue;
            }

            System.out.println(board.toString());
            
            // go through and find the user's move, if we can
            if(currentNode.getChildCount() > 0) {
                MoveNode tmpNode = currentNode.getBestChild();
                currentNode.clearChildren();    // so these can be GCed
                currentNode = tmpNode.findChild(userMove[0], userMove[1]);
                tmpNode.clearChildren();    // so these can be GCed
            }

            if(currentNode == null) {
                System.out.println("CREATING NEW NODE");
                currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
            }

            long start = System.currentTimeMillis();
            int[] aiMove = ai.computeNextMove(currentNode, Color.BLACK);
            long time = System.currentTimeMillis() - start;

            System.out.println(currentNode.childrenToString());
            System.out.println();

            double nodeCount = currentNode.getNodeCount();
            double nps = (nodeCount / (double)time) * 1000.0;
            System.out.println("TIME: " + time + " NODES: " + nodeCount + " NPS: " + nps);
            
            Runtime rt = Runtime.getRuntime();
            System.out.format("TOTAL MEM: %,d FREE MEM: %,d%n%n", rt.totalMemory(), rt.freeMemory());

            // print out the PGN move
            final String pgnMove = utils.computePgnMove(aiMove[0], aiMove[1]);

            System.out.println("MOVE: " + pgnMove);

            try {
                board.makeMove(aiMove[0], aiMove[1]);
            } catch (IllegalMoveException e) {
                System.err.println("Illegal computer move: " + e.getMessage());
                System.exit(-1);
            }

            System.out.println(board.toString());
        }
    }

}
