package jobs;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import models.IncomingTransaction;
import org.apache.log4j.Logger;
import org.bitcoin.stratum.*;
import org.bitcoinpotato.util.LogUtil;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Responsible for detecting new transactions.
 * In the future, this might be replaced with a notification API from Stratum Network.
 */
@Every("30s")
public class TransactionPoller extends Job {
    private final static Logger logger = LogUtil.getLogger();
    private final Stratum stratum = StratumHolder.Stratum;

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
        if (Play.runingInTestMode())
            return;

        logger.info("Transaction poller running");

        // While we can process transactions, we loop
        while (true) {
            IncomingTransaction lastValidTx = IncomingTransaction.getLatestValid();
            List<IncomingTransaction> newTransactions = IncomingTransaction.byStatusSortedChronologically(IncomingTransaction.Status.New);

            IncomingTransaction goodTransaction = findGoodTransaction(newTransactions, lastValidTx);
            if (goodTransaction != null) {
                handleNewValidTransaction(goodTransaction);

                // When the loop finishes, let's have another go, perhaps there's another new transaction that can be promoted
                continue;
            }

            refundInvalidTransactions();
            break;
        }
    }

    /**
     * All remaining funds at all our BTC addresses are refunded, because they were less than the minimal fee, or
     * paid to old addresses.
     */
    private void refundInvalidTransactions() {
        List<IncomingTransaction> allTx = IncomingTransaction.all().fetch();
        Collection<String> allPublicAddress = Collections2.transform(allTx, new Function<IncomingTransaction, String>() {
            public String apply(IncomingTransaction tx) {
                return tx.publicAddress;
            }
        });

        Map<String, AddressInfo> remoteTransactions = stratum.getAddressInfo(allPublicAddress);
        for (IncomingTransaction tx : allTx) {
            AddressInfo addressInfo = remoteTransactions.get(tx.publicAddress);
            if (addressInfo == null || addressInfo.balance.compareTo(stratum.getTransactionFee()) < 0) {
                // Too little BTC at this address, skipping
                continue;
            }

            stratum.sendTransaction(new OutgoingRemoteTransaction().addInputKey(tx.privateKey).addPayment(
                    new OutgoingRemotePayment(tx.payoutAddress, addressInfo.balance)));
        }
    }

    private void handleNewValidTransaction(IncomingTransaction goodTransaction) {
        BigDecimal commission = goodTransaction.actualAmount.multiply(commissionRate);
        BigDecimal netAmount = goodTransaction.actualAmount.subtract(commission);

        OutgoingRemoteTransaction outgoingTx = new OutgoingRemoteTransaction()
                .addInputKey(goodTransaction.privateKey)
                .addPayment(new OutgoingRemotePayment(goodTransaction.payoutAddress, netAmount))
                .addPayment(new OutgoingRemotePayment(housePublicAddress, commission));
        stratum.sendTransaction(outgoingTx);

        goodTransaction.status = IncomingTransaction.Status.Valid;
        goodTransaction.save();
    }

    /**
     * Find a good transaction: The first one that is new, and our address received at least the expected amount.
     */
    private IncomingTransaction findGoodTransaction(List<IncomingTransaction> incomingTransactions, IncomingTransaction lastValidTx) {
        for (IncomingTransaction tx : incomingTransactions) {
            AddressInfo info = stratum.getAddressInfo(tx.publicAddress);
            if (info.balance.compareTo(lastValidTx.nextMinimalAmount) >= 0) {
                tx.actualAmount = info.balance;
                return tx;
            }
        }
        return null;
    }
}
