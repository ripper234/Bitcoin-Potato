package org.bitcoin.stratum;

import java.math.BigDecimal;

public class AddressInfo {
    public final BigDecimal balance;

    public AddressInfo(BigDecimal balance) {
        this.balance = balance;
    }
}
