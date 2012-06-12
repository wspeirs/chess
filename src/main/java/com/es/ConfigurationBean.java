package com.es;

public class ConfigurationBean {
    private int transpositionTableSize;
    
    public ConfigurationBean() {
        this.transpositionTableSize = 100000;
    }

    public int getTranspositionTableSize() {
        return transpositionTableSize;
    }

    public void setTranspositionTableSize(int transpositionTableSize) {
        this.transpositionTableSize = transpositionTableSize;
    }
    
}
