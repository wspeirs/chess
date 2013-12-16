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
import com.es.ai.evaluate.IEvaluate;
import com.es.ai.evaluate.PositionOnlyEvaluate;
import com.es.pieces.Piece.Color;
import com.fluxchess.jcpi.models.GenericBoard;

public class NegaMaxSearchTest {
    public static final Logger LOG = LoggerFactory.getLogger(NegaMaxSearchTest.class);

    NegaMaxSearch negamax;
    IEvaluate eval = new PositionOnlyEvaluate();
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
        GenericBoard genericBoard = new GenericBoard("r3k3/8/8/8/8/8/8/2R3K1 w - - 0 1");
        Board board = new Board(genericBoard);

        negamax = new NegaMaxSearch(Color.WHITE, board, configuration, eval);

        MoveNode moveNode = negamax.computeNextMove(rootNode);

        System.out.println(board);
        System.out.println(moveNode.childrenToString());
    }
}
