package com.es.engines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.CmdConfiguration;
import com.es.IllegalMoveException;
import com.es.PgnUtils;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;
import com.es.pieces.Piece.Color;

public class GuiEngine implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(GuiEngine.class);

    private Configuration config;
    private Board board;
    private AlphaBetaAI ai;
    private PgnUtils utils;

    public GuiEngine(Configuration config) {
        this.config = config;
        this.board = new Board();
        this.ai = new AlphaBetaAI(Color.BLACK, board, config);
        this.utils = new PgnUtils(this.board);
    }

    @Override
    public void run() {
        MoveNode rootNode = new MoveNode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "go";

        // print out some config info
        System.out.println("DEPTH: " + config.getInt(CmdConfiguration.DEPTH));

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

                board.makeMove(Board.createMoveValue(userMove[0], userMove[1], '-'));
            } catch(IllegalMoveException e) {
                System.err.println("Illegal user move: " + e.getMessage());
                continue;
            }

            System.out.println(board.toString());

            // go through and find the user's move, if we can
            if(rootNode.getChildCount() > 0) {
                MoveNode tmpNode = rootNode.getBestChild();
                rootNode.clearChildren();    // so these can be GCed
                rootNode = tmpNode.findChild(Board.createMoveValue(userMove[0], userMove[1], '-'));
                tmpNode.clearChildren();    // so these can be GCed
            }

            if(rootNode == null) {
                LOG.info("COULDN'T FIND USER MOVE {} -> {}", Integer.toHexString(userMove[0]), Integer.toHexString(userMove[1]));
                rootNode = new MoveNode();
            } else {
                LOG.info("FOUND NODE FOR {} -> {}", Integer.toHexString(userMove[0]), Integer.toHexString(userMove[1]));
            }

            long start = System.currentTimeMillis();
            int aiMove = ai.computeNextMove(rootNode, Color.BLACK);
            long time = System.currentTimeMillis() - start;

            System.out.println(rootNode.childrenToString());
            System.out.println();

            double nodeCount = rootNode.getNodeCount();
            double nps = (nodeCount / (double)time) * 1000.0;
            System.out.println("TIME: " + time + " NODES: " + nodeCount + " NPS: " + nps);

            Runtime rt = Runtime.getRuntime();
            System.out.format("TOTAL MEM: %,d FREE MEM: %,d%n%n", rt.totalMemory(), rt.freeMemory());

            // print out the PGN move
            final String pgnMove = utils.computePgnMove(aiMove);

            System.out.println("MOVE: " + pgnMove);

            try {
                board.makeMove(aiMove);
            } catch (IllegalMoveException e) {
                System.err.println("Illegal computer move: " + e.getMessage());
                System.exit(-1);
            }

            System.out.println(board.toString());
        }
    }

}
