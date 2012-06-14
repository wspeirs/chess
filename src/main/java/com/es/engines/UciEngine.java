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
import com.es.pieces.Piece.Color;

public class UciEngine extends AbstractEngine implements Engine {
    private static final Logger LOG = LoggerFactory.getLogger(UciEngine.class);

    private Configuration config;
    private Board board;
    private Color color;
    private static final Map<GenericPosition, Integer> positions = new EnumMap<GenericPosition, Integer>(GenericPosition.class);
    private MoveNode currentNode;
    private int moveCount;
    private int[] lastOpponentMove;

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

        // Setup the new game
        currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        board = new Board();
        moveCount = 0;
    }

    public void visit(EngineAnalyzeCommand command) {
        LOG.info("Engine Analyze: color={}", command.board.getActiveColor().toChar());
        
        if(LOG.isInfoEnabled()) {
            LOG.info("MOVE LIST SIZE: {}", command.moveList.size());
            LOG.info("BOARD SENT:");
            LOG.info(command.board.toString());
        }

        // update the board with the moves made
        if(command.moveList.size() != 0) {
            GenericMove move = command.moveList.get(command.moveList.size()-1);
            lastOpponentMove = new int[] { positions.get(move.from), positions.get(move.to) };
            
            try {
                board.makeMove(lastOpponentMove[0], lastOpponentMove[1]);
            } catch (IllegalMoveException e) {
                LOG.error("Illegal move: {}", e.getMessage());
                new EngineQuitCommand().accept(this);
            }
            
            // update the color
            if(command.board.getActiveColor().toChar() == 'w') {
                color = Color.BLACK;
            } else {
                color = Color.WHITE;
            }
        } else {
            color = Color.WHITE;
        }
    }

    public void visit(EngineStartCalculatingCommand command) {
        LOG.info("Engine Start Calculating");
        
        LOG.debug("CREATED AI WITH COLOR: {}", color);
        AlphaBetaAI ai = new AlphaBetaAI(color);    // create the AI

        LOG.debug("CUR NODE CHILD COUNT: {}", currentNode.getChildCount());
        
        // go through and find the user's move, if we can
        if(currentNode.getChildCount() > 0) {
            MoveNode tmpNode = currentNode.getBestChild();
            currentNode.clearChildren();    // so these can be GCed
            currentNode = tmpNode.findChild(lastOpponentMove[0], lastOpponentMove[1]);
            tmpNode.clearChildren();    // so these can be GCed
        }

        if(currentNode == null) {
            LOG.info("CREATING NEW NODE");
            currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        }

        LOG.debug("COMPUTING NEXT MOVE FOR: {}", color);
        int[] aiMove = ai.computeNextMove(currentNode, color);
        LOG.debug("FOUND MOVE: {} -> {}", Integer.toHexString(aiMove[0]), Integer.toHexString(aiMove[1]));

        GenericFile file = GenericFile.values()[aiMove[0] % 16];
        GenericRank rank = GenericRank.values()[aiMove[0] >>> 4];
        GenericPosition from = GenericPosition.valueOf(file, rank);

        file = GenericFile.values()[aiMove[1] % 16];
        rank = GenericRank.values()[aiMove[1] >>> 4];
        GenericPosition to = GenericPosition.valueOf(file, rank);
        
        if(LOG.isInfoEnabled()) {
            LOG.info("SENDING MOVE: {} -> {}", from, to);
            
            LOG.info(currentNode.childrenToString());
        }
        
        GenericMove genericMove = new GenericMove(from, to);
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
