package com.bitcoinpotato.overlay;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class Transaction {
    public DateTime timestamp;
    public String fromAddress;
    public String toAddress;
    public Integer confirmations;
    public BigDecimal amount;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
