package com.es;

public class IllegalMoveException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public IllegalMoveException(String reason) {
        super(reason);
    }

}
