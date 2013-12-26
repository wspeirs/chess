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

public class NegaScoutSearchTest {
    public static final Logger LOG = LoggerFactory.getLogger(NegaScoutSearchTest.class);

    NegaScoutSearch negaScout;
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
        GenericBoard genericBoard = new GenericBoard("4k2r/8/8/8/8/8/8/RK6 w k - 0 1");
        Board board = new Board(genericBoard);
        eval = new PieceOnlyEvaluate(Color.fromGenericColor(genericBoard.getActiveColor()));

        negaScout = new NegaScoutSearch(Color.WHITE, board, configuration, eval);

        MoveNode moveNode = negaScout.computeNextMove(rootNode);

        System.out.println(board);
        System.out.println(moveNode.childrenToString(false));
    }
}
