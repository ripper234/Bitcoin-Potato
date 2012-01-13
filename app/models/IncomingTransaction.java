package models;

import org.bitcoin.stratum.KeyPair;
import play.data.validation.Unique;

import java.math.BigDecimal;
import java.util.List;

@javax.persistence.Entity
public class IncomingTransaction extends TemporalModel {
    public IncomingTransaction() {
    }

    public IncomingTransaction(KeyPair keyPair, String payoutAddress, BigDecimal minimalAmount) {
        this.publicAddress = keyPair.publicAddress;
        this.privateKey = keyPair.privateKey;
        this.payoutAddress = payoutAddress;
        this.nextMinimalAmount = minimalAmount;
        this.status = Status.New;
    }

    @Unique
    public String publicAddress;
    public String payoutAddress;
    public BigDecimal nextMinimalAmount;
    public BigDecimal actualAmount;
    public byte[] privateKey;

    public enum Status {
        New,
        Valid,

        Refunded
    }

    public Status status;

    public static IncomingTransaction getLatestValid() {
        return find("status = ? order by created", Status.Valid).first();
    }

    public static List<IncomingTransaction> byStatusSortedChronologically(Status status) {
        return find("byStatus order by created", status).fetch();
    }
}
