package controllers;

import models.IncomingTransaction;

import java.math.BigDecimal;

public class BookKeeper {
    private final static BigDecimal velocity = new BigDecimal("1.2");

    public static BigDecimal nextMinimalAmount(IncomingTransaction previousTransaction) {
        return velocity.multiply(previousTransaction.actualAmount);
    }
}
