package org.bitcoin.stratum;

import com.google.bitcoin.core.Transaction;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Transaction information received from a remote Stratum server.
 * Includes extra information like number of confirmations.
 */
public class RemoteTransaction {
    private final Transaction transaction;
    private final int confirmations;

    public RemoteTransaction(Transaction transaction, int confirmations) {
        this.transaction = transaction;
        this.confirmations = confirmations;
    }


    public Transaction getTransaction() {
        return transaction;
    }

    public int getConfirmations() {
        return confirmations;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
