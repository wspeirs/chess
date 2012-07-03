package com.es.engines;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import jcpi.AbstractCommunication;
import jcpi.AbstractEngine;
import jcpi.commands.EngineAnalyzeCommand;
import jcpi.commands.EngineDebugCommand;
import jcpi.commands.EngineInitializeRequestCommand;
import jcpi.commands.EngineNewGameCommand;
import jcpi.commands.EnginePonderHitCommand;
import jcpi.commands.EngineQuitCommand;
import jcpi.commands.EngineReadyRequestCommand;
import jcpi.commands.EngineSetOptionCommand;
import jcpi.commands.EngineStartCalculatingCommand;
import jcpi.commands.EngineStopCalculatingCommand;
import jcpi.commands.GuiBestMoveCommand;
import jcpi.commands.GuiInformationCommand;
import jcpi.commands.GuiInitializeAnswerCommand;
import jcpi.commands.GuiReadyAnswerCommand;
import jcpi.data.GenericFile;
import jcpi.data.GenericMove;
import jcpi.data.GenericPosition;
import jcpi.data.GenericRank;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.IllegalMoveException;
import com.es.ai.AlphaBetaAI;
import com.es.ai.MoveNode;

public class UciEngine extends AbstractEngine implements Engine {
    private static final Logger LOG = LoggerFactory.getLogger(UciEngine.class);

    private Configuration config;
    private Board board;
    private static final Map<GenericPosition, Integer> positions = new EnumMap<GenericPosition, Integer>(GenericPosition.class);
    private MoveNode currentNode;

    static {
        for (GenericPosition position : GenericPosition.values()) {
            int file = Arrays.asList(GenericFile.values()).indexOf(position.file);
            int rank = Arrays.asList(GenericRank.values()).indexOf(position.rank);

            int intPosition = rank * 16 + file;

            positions.put(position, intPosition);
        }
    }

    public UciEngine(Configuration config, AbstractCommunication communication) {
        super(communication);
        this.config = config;
    }

    public void visit(EngineInitializeRequestCommand command) {
        LOG.info("Engine Initialize Request");
        new EngineStopCalculatingCommand().accept(this);
        this.communication.send(new GuiInitializeAnswerCommand("chess 1.0", "William Speirs"));
    }

    public void visit(EngineSetOptionCommand command) {
        LOG.info("Engine Set Option: name={} value={}", command.name, command.value);
    }

    public void visit(EngineDebugCommand command) {
        LOG.info("Engine Debug Command: debug={} toggle={}", command.debug, command.toggle);

        GuiInformationCommand infoCommand = new GuiInformationCommand();

        if(LOG.isDebugEnabled()) {
            infoCommand.setString("Turning off debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.INFO);
        } else {
            infoCommand.setString("Turning on debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.DEBUG);
        }

        this.communication.send(infoCommand);
    }

    public void visit(EngineReadyRequestCommand command) {
        LOG.info("Engine Ready Request: {}", command.token);

        // Send the token back
        this.communication.send(new GuiReadyAnswerCommand(command.token));
    }

    public void visit(EngineNewGameCommand command) {
        LOG.info("Engine New Game");

        // It might be good to stop computing first...
        new EngineStopCalculatingCommand().accept(this);
    }

    public void visit(EngineAnalyzeCommand command) {
        LOG.info("Engine Analyze: color={}", command.board.getActiveColor().toChar());

        if(LOG.isInfoEnabled()) {
            LOG.info("MOVE LIST SIZE: {}", command.moveList.size());
            LOG.info("BOARD SENT:");
            LOG.info(command.board.toString());
        }

        // Setup the new game
        // We can receive any board here! So mirror the GenericBoard on our internal board.
        board = new Board(command.board);
        currentNode = new MoveNode(board, null, Board.MAX_SQUARE);

		// Make all moves here! UCI is not stateful! So we have to setup the board as the protocol says.
		// Don't just take the last move!
		List<GenericMove> moveList = command.moveList;
		for (GenericMove move : moveList) {
			try {
			    // TODO: Add in piece promotion here
			    final int moveValue = Board.createMoveValue(positions.get(move.from), positions.get(move.to), '-');
				this.board.makeMove(moveValue);
			} catch (IllegalMoveException e) {
                LOG.error("Illegal move: {}", e.getMessage(), e);
                new EngineQuitCommand().accept(this);
			}
		}
    }

    public void visit(EngineStartCalculatingCommand command) {
        LOG.info("Engine Start Calculating");

		// The game state is now encoded within the board. Get everything from the board now.
        LOG.debug("CREATED AI WITH COLOR: {}", board.getActiveColor());
        AlphaBetaAI ai = new AlphaBetaAI(board.getActiveColor(), config);    // create the AI

        LOG.debug("CUR NODE CHILD COUNT: {}", currentNode.getChildCount());

        currentNode = new MoveNode(board, null, Board.MAX_SQUARE);

        LOG.debug("COMPUTING NEXT MOVE FOR: {}", board.getActiveColor());

        long start = System.currentTimeMillis();
        int aiMove = ai.computeNextMove(currentNode, board.getActiveColor());
        long time = System.currentTimeMillis() - start;
        
        final int aiFrom = Board.getFromSquare(aiMove);
        final int aiTo = Board.getToSquare(aiMove);

        LOG.debug("FOUND MOVE: {} -> {}", Integer.toHexString(aiFrom), Integer.toHexString(aiTo));

        GenericFile file = GenericFile.values()[aiFrom % 16];
        GenericRank rank = GenericRank.values()[aiFrom >>> 4];
        GenericPosition from = GenericPosition.valueOf(file, rank);

        file = GenericFile.values()[aiTo % 16];
        rank = GenericRank.values()[aiTo >>> 4];
        GenericPosition to = GenericPosition.valueOf(file, rank);

        if(LOG.isInfoEnabled()) {
            LOG.info("SENDING MOVE: {} -> {}", from, to);

            LOG.info(currentNode.childrenToString());
        }

        try {
            // make the move on the board
            board.makeMove(aiMove);
        } catch (IllegalMoveException e) {
            LOG.error("AI MADE AN ILLEGAL MOVE: {}", e.getMessage());
            new EngineQuitCommand().accept(this);
        }

        GenericMove genericMove = new GenericMove(from, to);

        GuiInformationCommand infoCmd = new GuiInformationCommand();

        infoCmd.setCurrentMove(genericMove);
        infoCmd.setDepth(4);
        infoCmd.setTime(time);

        communication.send(infoCmd);

        this.communication.send(new GuiBestMoveCommand(genericMove, null));
    }

    public void visit(EngineStopCalculatingCommand command) {
        LOG.info("Engine Stop Calculating");
    }

    public void visit(EnginePonderHitCommand command) {
        LOG.info("Engine Ponder Hit");
        // We have a ponder hit, it's your turn now!
    }

    @Override
    protected void quit() {
        LOG.info("Quit");
        new EngineStopCalculatingCommand().accept(this);
    }

    @Override
    public void play() {
        this.run();
    }

}
