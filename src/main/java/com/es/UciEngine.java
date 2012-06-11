package com.es;

import java.util.List;

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
import jcpi.data.GenericMove;
import jcpi.data.GenericPosition;
import jcpi.data.IllegalNotationException;
import jcpi.standardio.StandardIoCommunication;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.pieces.Piece.Color;

public class UciEngine extends AbstractEngine {
    private static final Logger LOG = LoggerFactory.getLogger(UciEngine.class);
    
    private Board board;
    private Color color;

    public static void main(String[] args) {
        // Choose and create a communication channel. For now there exists only
        // an object for the standard io communication.
        AbstractCommunication communication = new StandardIoCommunication();
        
        // Create your engine.
        AbstractEngine engine = new UciEngine(communication);

        // Start the engine.
        engine.run();
    }

    public UciEngine(AbstractCommunication communication) {
        super(communication);
    }

    public void visit(EngineInitializeRequestCommand command) {
        LOG.info("Engine Initialize Request");
        new EngineStopCalculatingCommand().accept(this);
        this.communication.send(new GuiInitializeAnswerCommand("chess 1.0", "William Speirs"));
    }

    public void visit(EngineSetOptionCommand command) {
        LOG.info("Engine Set Option");
    }

    public void visit(EngineDebugCommand command) {
        LOG.info("Engine Debug Command");
        
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
        LOG.info("Engine Ready Request");

        // Send the token back
        this.communication.send(new GuiReadyAnswerCommand(command.token));
    }

    public void visit(EngineNewGameCommand command) {
        LOG.info("Engine New Game");

        // It might be good to stop computing first...
        new EngineStopCalculatingCommand().accept(this);
        
        // Setup the new game

        // Don't start computing though!
    }

    public void visit(EngineAnalyzeCommand command) {
        LOG.info("Engine Analyze");

        // Setup the board & color
        board = new Board();
        color = Color.WHITE;
        
        PgnUtils utils = new PgnUtils(board);

        try {
            List<GenericMove> moveList = command.moveList;
            for (GenericMove move : moveList) {
                int[] userMove = utils.parseSingleMove(color, command.board.getPiece(move.from).toChar() + move.toString());
                board.makeMove(userMove[0], userMove[1]);
                
                if (color == Color.WHITE) {
                    color = Color.BLACK;
                } else {
                    color = Color.WHITE;
                }
            }
        } catch (IllegalMoveException e) {
            new EngineQuitCommand().accept(this);
        }
    }

    public void visit(EngineStartCalculatingCommand command) {
        LOG.info("Engine Start Calculating");
        
        MoveAI ai = new MoveAI(color);    // create the AI

        MoveNode currentNode = ai.findNode(board);
        if(currentNode == null) {
            currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });
        }

        int[] aiMove = ai.computeNextMove(currentNode, color);

        PgnUtils utils = new PgnUtils(board);
        String move = utils.computePgnMove(aiMove[0], aiMove[1]).substring(1);
        
        try {
            GenericMove genericMove = new GenericMove(move);
            this.communication.send(new GuiBestMoveCommand(genericMove, null));
        } catch (IllegalNotationException e) {
            new EngineQuitCommand().accept(this);
        }
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
        new EngineStopCalculatingCommand().accept(this);
    }

}
