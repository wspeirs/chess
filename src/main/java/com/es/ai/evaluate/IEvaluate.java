package com.es.ai.evaluate;

import com.es.Board;
import com.es.pieces.Piece.Color;


public interface IEvaluate {

    public int evaluate(Board board, Color maxColor);
}
