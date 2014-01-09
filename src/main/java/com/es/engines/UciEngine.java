package com.es.engines;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.Board;
import com.es.IllegalMoveException;
import com.es.ai.MoveNode;
import com.es.ai.search.AlphaBetaAI;
import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.EngineAnalyzeCommand;
import com.fluxchess.jcpi.commands.EngineDebugCommand;
import com.fluxchess.jcpi.commands.EngineInitializeRequestCommand;
import com.fluxchess.jcpi.commands.EngineNewGameCommand;
import com.fluxchess.jcpi.commands.EnginePonderHitCommand;
import com.fluxchess.jcpi.commands.EngineQuitCommand;
import com.fluxchess.jcpi.commands.EngineReadyRequestCommand;
import com.fluxchess.jcpi.commands.EngineSetOptionCommand;
import com.fluxchess.jcpi.commands.EngineStartCalculatingCommand;
import com.fluxchess.jcpi.commands.EngineStopCalculatingCommand;
import com.fluxchess.jcpi.commands.IProtocol;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.commands.ProtocolInformationCommand;
import com.fluxchess.jcpi.commands.ProtocolInitializeAnswerCommand;
import com.fluxchess.jcpi.commands.ProtocolReadyAnswerCommand;
import com.fluxchess.jcpi.models.GenericFile;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.GenericRank;
import com.fluxchess.jcpi.protocols.UciProtocol;

public class UciEngine extends AbstractEngine {
    private static final Logger LOG = LoggerFactory.getLogger(UciEngine.class);

    private Configuration config;
    private Board board;
    private static final Map<GenericPosition, Integer> positions = new EnumMap<GenericPosition, Integer>(GenericPosition.class);
    private MoveNode currentNode;
    private IProtocol protocol;

    static {
        for (GenericPosition position : GenericPosition.values()) {
            int file = Arrays.asList(GenericFile.values()).indexOf(position.file);
            int rank = Arrays.asList(GenericRank.values()).indexOf(position.rank);

            int intPosition = rank * 16 + file;

            positions.put(position, intPosition);
        }
    }

    public UciEngine(Configuration config, UciProtocol protocol) {
        super(protocol);
        this.config = config;
        this.protocol = protocol;
    }

    @Override
    protected void quit() {
        LOG.info("Quit");
        new EngineStopCalculatingCommand().accept(this);
    }

    @Override
    public void receive(EngineInitializeRequestCommand command) {
        LOG.info("Engine Initialize Request");
        new EngineStopCalculatingCommand().accept(this);
        protocol.send(new ProtocolInitializeAnswerCommand("chess 1.0", "William Speirs"));
    }

    @Override
    public void receive(EngineSetOptionCommand command) {
        LOG.info("Engine Set Option: name={} value={}", command.name, command.value);
    }

    @Override
    public void receive(EngineDebugCommand command) {
        LOG.info("Engine Debug Command: debug={} toggle={}", command.debug, command.toggle);

        final ProtocolInformationCommand infoCommand = new ProtocolInformationCommand();

        if(LOG.isDebugEnabled()) {
            infoCommand.setString("Turning off debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.INFO);
        } else {
            infoCommand.setString("Turning on debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.DEBUG);
        }

        protocol.send(infoCommand);
    }

    @Override
    public void receive(EngineReadyRequestCommand command) {
        LOG.info("Engine Ready Request: {}", command.token);

        // Send the token back
        protocol.send(new ProtocolReadyAnswerCommand(command.token));
    }

    @Override
    public void receive(EngineNewGameCommand command) {
        LOG.info("Engine New Game");

        // It might be good to stop computing first...
        new EngineStopCalculatingCommand().accept(this);
    }

    @Override
    public void receive(EngineAnalyzeCommand command) {
        LOG.info("Engine Analyze: color={}", command.board.getActiveColor().toChar());

        if(LOG.isInfoEnabled()) {
            LOG.info("MOVE LIST SIZE: {}", command.moves.size());
            LOG.info("BOARD SENT:");
            LOG.info(command.board.toString());
        }

        // Setup the new game
        // We can receive any board here! So mirror the GenericBoard on our internal board.
        board = new Board(command.board);
        currentNode = new MoveNode();

        // Make all moves here! UCI is not stateful! So we have to setup the board as the protocol says.
        // Don't just take the last move!
        List<GenericMove> moveList = command.moves;
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

    @Override
    public void receive(EngineStartCalculatingCommand command) {
        LOG.info("Engine Start Calculating");

        // The game state is now encoded within the board. Get everything from the board now.
        LOG.debug("CREATED AI WITH COLOR: {}", board.getActiveColor());
        AlphaBetaAI ai = new AlphaBetaAI(board.getActiveColor(), board, config);    // create the AI

        LOG.debug("CUR NODE CHILD COUNT: {}", currentNode.getChildCount());

        currentNode = new MoveNode();

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

            LOG.info(currentNode.childrenToString(false));
        }

        try {
            // make the move on the board
            board.makeMove(aiMove);
        } catch (IllegalMoveException e) {
            LOG.error("AI MADE AN ILLEGAL MOVE: {}", e.getMessage());
            new EngineQuitCommand().accept(this);
        }

        GenericMove genericMove = new GenericMove(from, to);

        ProtocolInformationCommand infoCmd = new ProtocolInformationCommand();

        infoCmd.setCurrentMove(genericMove);
        infoCmd.setDepth(4);
        infoCmd.setTime(time);

        protocol.send(infoCmd);
        protocol.send(new ProtocolBestMoveCommand(genericMove, null));
    }

    @Override
    public void receive(EngineStopCalculatingCommand command) {
        LOG.info("Engine Stop Calculating");
    }

    @Override
    public void receive(EnginePonderHitCommand command) {
        LOG.info("Engine Ponder Hit");
        // We have a ponder hit, it's your turn now!
    }

}
