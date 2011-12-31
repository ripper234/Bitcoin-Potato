package com.bitcoinpotato.overlay;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class Transaction {
    public DateTime timestamp;
    public String fromAddress;
    public String toAddress;
    public Integer confirmations;
    public BigDecimal amount;
}
