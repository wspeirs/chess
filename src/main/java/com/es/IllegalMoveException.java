package com.es;

public class IllegalMoveException extends Exception {

    private static final long serialVersionUID = 1L;
    private final boolean kingInCheck;

    public IllegalMoveException(String reason) {
        super(reason);
        this.kingInCheck = false;
    }

    public IllegalMoveException(String reason, boolean kingInCheck) {
        super(reason);
        this.kingInCheck = kingInCheck;
    }

    public boolean isKingInCheck() {
        return kingInCheck;
    }
}
