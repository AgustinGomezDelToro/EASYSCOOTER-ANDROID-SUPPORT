package com.example.easyscootersapp.data;

public class EsError extends Exception {
    final public ErrorCode code;
    public EsError(ErrorCode code) {
        this.code = code;
    }
}

