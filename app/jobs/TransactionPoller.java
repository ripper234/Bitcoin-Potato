package jobs;

import com.bitcoinpotato.overlay.StratumHolder;
import com.bitcoinpotato.overlay.Transaction;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import models.ExpectedTransaction;
import org.apache.log4j.Logger;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import util.LogUtil;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Responsible for detecting new transactions.
 * In the future, this might be replaced with a notification API from Stratum Network.
 */
@Every("30s")
public class TransactionPoller extends Job {
    private final static Logger logger = LogUtil.getLogger();
    /**
     * How fast the game will move - each payment must be at least this amount times the previous payment.
     */
    private final static BigDecimal velocity = new BigDecimal("1.2");

    /**
     * How much of each transaction goes to the house.
     */
    private final static BigDecimal commissionRate = new BigDecimal("0.01");

    private final static String housePublicAddress = (String) Play.configuration.get("housePublicAddress");

    @Override
    public void doJob() throws Exception {
        ExpectedTransaction expectedTransaction = ExpectedTransaction.getLatest();
        List<Transaction> incomingTransactions = StratumHolder.Stratum.getIncomingTransactions(expectedTransaction.publicAddress);

        incomingTransactions = discardInvalidTransactions(incomingTransactions, expectedTransaction.minimalAmount);
        if (incomingTransactions.isEmpty()) {
            // nothing to do for now
            return;
        }

        // Check if there is more than one valid transaction.
        // The odds of this happening is slim, since we're polling every ten seconds.
        // Let's log it and pick the first
        logger.warn(String.format("More than one transaction detected into %s: %s",
                expectedTransaction.publicAddress,
                Joiner.on(",").join(incomingTransactions)));

        // Set up the next expected transaction
        Transaction actualTransaction = incomingTransactions.get(0);
        BigDecimal nextMinimalAmount = actualTransaction.amount.max(expectedTransaction.minimalAmount.multiply(velocity));
        ExpectedTransaction nextExpectedTx = new ExpectedTransaction(StratumHolder.Stratum.newKeyPair(), actualTransaction.fromAddress, nextMinimalAmount);

        BigDecimal commission = commissionRate.multiply(actualTransaction.amount);
        BigDecimal payout = actualTransaction.amount.subtract(commission);

        // TODO - Ideally, this next part should all be in one distributed transaction
        // Think about some clever thing to do in case of partial failure here.
        StratumHolder.Stratum.sendTransaction(expectedTransaction.privateKey, expectedTransaction.payoutAddress, payout);
        StratumHolder.Stratum.sendTransaction(expectedTransaction.privateKey, housePublicAddress, payout);
        nextExpectedTx.save();
    }

    /**
     * Discard and log any transactions that are lower than the minimum.
     */
    private List<Transaction> discardInvalidTransactions(List<Transaction> incomingTransactions, final BigDecimal minimalAmount) {
        return newArrayList(Collections2.filter(incomingTransactions, new Predicate<Transaction>() {
            @Override
            public boolean apply(Transaction transaction) {
                return transaction.amount.compareTo(minimalAmount) >= 0 &&
                        transaction.confirmations > 0;
            }
        }));
    }
}
