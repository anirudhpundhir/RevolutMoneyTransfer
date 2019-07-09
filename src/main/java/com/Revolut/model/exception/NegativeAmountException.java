package com.Revolut.model.exception;

public class NegativeAmountException extends RuntimeException {
    private String operation;

    public NegativeAmountException(String operation) {
        this.operation = operation;
    }

    @Override
    public String getMessage() {
        return "Can't " + operation + " negative amount of money";
    }
}