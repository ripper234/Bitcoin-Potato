package org.bitcoin.stratum;

import java.math.BigDecimal;

public class OutgoingRemotePayment {
    private final String toAddress;
    private final BigDecimal amount;

    public OutgoingRemotePayment(String toAddress, BigDecimal amount) {
        this.toAddress = toAddress;
        this.amount = amount;
    }

    public String getToAddress() {
        return toAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
