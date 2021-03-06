package com.es.ai;

import java.util.LinkedHashMap;
import java.util.Map;

import com.es.Board;

public class TranspositionTable extends LinkedHashMap<Board, MoveNode> {

    private static final long serialVersionUID = 1L;
    private static final int CAPACITY = 100000;

    public TranspositionTable() {
        super(CAPACITY + 1, 1.1f, false);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Board,MoveNode> eldest) {
        return super.size() > CAPACITY;
    }
}
