package com.es;

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
import jcpi.standardio.StandardIoCommunication;

import com.es.pieces.Piece.Color;

public class UciEngine extends AbstractEngine {
//    private static final Logger LOG = LoggerFactory.getLogger(UciEngine.class);

    private Board board;
    private Color color;
    private static final Map<GenericPosition, Integer> positions = new EnumMap<GenericPosition, Integer>(GenericPosition.class);

    static {
        for (GenericPosition position : GenericPosition.values()) {
            int file = Arrays.asList(GenericFile.values()).indexOf(position.file);
            int rank = Arrays.asList(GenericRank.values()).indexOf(position.rank);

            int intPosition = rank * 16 + file;

            positions.put(position, intPosition);
        }
    }

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
//        LOG.info("Engine Initialize Request");
        new EngineStopCalculatingCommand().accept(this);
        this.communication.send(new GuiInitializeAnswerCommand("chess 1.0", "William Speirs"));
    }

    public void visit(EngineSetOptionCommand command) {
//        LOG.info("Engine Set Option");
    }

    public void visit(EngineDebugCommand command) {
//        LOG.info("Engine Debug Command");

        GuiInformationCommand infoCommand = new GuiInformationCommand();
/*
        if(LOG.isDebugEnabled()) {
            infoCommand.setString("Turning off debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.INFO);
        } else {
            infoCommand.setString("Turning on debugging mode");
            LogManager.getLogger(UciEngine.class).setLevel(Level.DEBUG);
        }
*/
        this.communication.send(infoCommand);
    }

    public void visit(EngineReadyRequestCommand command) {
//        LOG.info("Engine Ready Request");

        // Send the token back
        this.communication.send(new GuiReadyAnswerCommand(command.token));
    }

    public void visit(EngineNewGameCommand command) {
//        LOG.info("Engine New Game");

        // It might be good to stop computing first...
        new EngineStopCalculatingCommand().accept(this);

        // Setup the new game

        // Don't start computing though!
    }

    public void visit(EngineAnalyzeCommand command) {
//        LOG.info("Engine Analyze");

        // Setup the board & color
        board = new Board();
        color = Color.WHITE;

        try {
            List<GenericMove> moveList = command.moveList;
            for (GenericMove move : moveList) {
                board.makeMove(positions.get(move.from), positions.get(move.to));

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
//        LOG.info("Engine Start Calculating");

        AlphaBetaAI ai = new AlphaBetaAI(color);    // create the AI

        MoveNode currentNode = new MoveNode(board, null, new int[] { Board.MAX_SQUARE, Board.MAX_SQUARE });

        int[] aiMove = ai.computeNextMove(currentNode, color);

        GenericFile file = GenericFile.values()[aiMove[0] % 16];
        GenericRank rank = GenericRank.values()[aiMove[0] >>> 4];
        GenericPosition from = GenericPosition.valueOf(file, rank);

        file = GenericFile.values()[aiMove[1] % 16];
        rank = GenericRank.values()[aiMove[1] >>> 4];
        GenericPosition to = GenericPosition.valueOf(file, rank);

        GenericMove genericMove = new GenericMove(from, to);
        this.communication.send(new GuiBestMoveCommand(genericMove, null));
    }

    public void visit(EngineStopCalculatingCommand command) {
//        LOG.info("Engine Stop Calculating");
    }

    public void visit(EnginePonderHitCommand command) {
//        LOG.info("Engine Ponder Hit");
        // We have a ponder hit, it's your turn now!
    }

    @Override
    protected void quit() {
        new EngineStopCalculatingCommand().accept(this);
    }

}
