package com.es.ai.search;

import static org.mockito.Mockito.when;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.es.Board;
import com.es.CmdConfiguration;
import com.es.ai.MoveNode;
import com.es.ai.evaluate.AbstractEvaluate;
import com.es.ai.evaluate.PieceOnlyEvaluate;
import com.es.pieces.Piece.Color;
import com.fluxchess.jcpi.models.GenericBoard;

public class MiniMaxSearchTest {
    public static final Logger LOG = LoggerFactory.getLogger(MiniMaxSearchTest.class);

    MiniMaxSearch minimax;
    AbstractEvaluate eval;
    MoveNode rootNode = new MoveNode();

    @Mock Configuration configuration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(configuration.getInt(CmdConfiguration.DEPTH)).thenReturn(4);
    }

    @Test
    public void testComputeNextMove() throws Exception {
        // Setup a new board from fen
        GenericBoard genericBoard = new GenericBoard("3k4/3r4/8/8/8/8/3R4/3K4 w - - 0 1");
        Board board = new Board(genericBoard);
        eval = new PieceOnlyEvaluate(Color.fromGenericColor(genericBoard.getActiveColor()));

        minimax = new MiniMaxSearch(Color.WHITE, board, configuration, eval);

        MoveNode moveNode = minimax.computeNextMove(rootNode);

        System.out.println(board);
        System.out.println(moveNode.childrenToString(false));
    }
}
