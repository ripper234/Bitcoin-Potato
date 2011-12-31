package models;

import com.bitcoinpotato.overlay.KeyPair;

import java.math.BigDecimal;

@javax.persistence.Entity
public class ExpectedTransaction extends TemporalModel {
    public ExpectedTransaction() {
    }

    public ExpectedTransaction(KeyPair keyPair, String payoutAddress, BigDecimal minimalAmount) {
        this.publicAddress = keyPair.publicAddress;
        this.privateKey = keyPair.privateKey;
        this.payoutAddress = payoutAddress;
        this.minimalAmount = minimalAmount;
    }

    public String fromAddress;
    public String publicAddress;
    public String payoutAddress;
    public BigDecimal minimalAmount;
    public BigDecimal actualAmount;
    public byte[] privateKey;

    /**
     * This assumes that no two entities have the same timestamp
     */
    public static ExpectedTransaction getLatest() {
        return find("order by created").first();
    }
}

