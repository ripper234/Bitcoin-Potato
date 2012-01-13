package jobs;

import com.bitcoinpotato.util.LogUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import models.IncomingTransaction;
import org.apache.log4j.Logger;
import org.bitcoin.stratum.*;
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


    private void foo() {
//        List<RemoteTransaction> incomingTransactions = StratumHolder.Stratum.getRemoteTransactions(incomingTransactions.publicAddress);
//
//                    incomingTransactions = discardInvalidTransactions(incomingTransactions, incomingTransactions.nextMinimalAmount);
//                    if (incomingTransactions.isEmpty()) {
//                        // nothing to do for now
//                        logger.debug("No incoming transactions found");
//                        return;
//                    }
//
//                    // Check if there is more than one valid transaction.
//                    // The odds of this happening is slim, since we're polling every ten seconds.
//                    // Let's log it and pick the first
//                    if (incomingTransactions.size() > 1) {
//                        logger.warn(String.format("More than one transaction detected into %s: %s",
//                                incomingTransactions.publicAddress,
//                                Joiner.on(",").join(incomingTransactions)));
//
//                        // let's not make any progress in this state (better safe than sorry)
//                        return;
//                    }
//
//                    // Set up the next expected transaction
//                    RemoteTransaction actualTransaction = Collections3.single(incomingTransactions);
//
//                    logger.info("Got transaction: " + actualTransaction);
//
//                    // Sanity check - the lower transactions are actually filtered beforehand.
//                    TransactionOutput output = findOurOutputs(actualTransaction.getTransaction().getOutputs(), incomingTransactions.publicAddress);
//                    BigDecimal btcOutput = Stratum.satoshisToBitcoin(output.getValue());
//                    checkArgument(btcOutput.compareTo(incomingTransactions.nextMinimalAmount) >= 0);
//
//                    BigDecimal nextMinimalAmount = btcOutput.multiply(velocity);
//
//                    List<TransactionInput> inputs = actualTransaction.getTransaction().getInputs();
//                    checkArgument(inputs.size() > 0);
//                    IncomingTransaction nextIncomingTx = new IncomingTransaction(StratumHolder.Stratum.newKeyPair(), inputs.get(0).getFromAddress().toString(), nextMinimalAmount);
//
//                    logger.info("Next payment will be at least " + nextIncomingTx.nextMinimalAmount + " BTC");
//
//                    BigDecimal commission = commissionRate.multiply(btcOutput);
//                    BigDecimal payout = btcOutput.subtract(commission);
//
//                    // TODO - Ideally, this next part should all be in one distributed transaction
//                    // Think about some clever thing to do in case of partial failure here.
//                    OutgoingRemoteTransaction outgoingTx = new OutgoingRemoteTransaction()
//                            .addPayment(new OutgoingRemotePayment(incomingTransactions.payoutAddress, payout))
//                            .addPayment(new OutgoingRemotePayment(housePublicAddress, payout))
//                            .addInputKey(incomingTransactions.privateKey);
//
//                    StratumHolder.Stratum.sendTransaction(outgoingTx);
//                    nextIncomingTx.save();
//
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

//    /**
//     * Returns those outputs that match our address
//     */
//    private TransactionOutput findOurOutputs(List<TransactionOutput> outputs, final String publicAddress) {
//        return Collections3.single(Collections2.filter(outputs, new Predicate<TransactionOutput>() {
//            @Override
//            public boolean apply(TransactionOutput transactionOutput) {
//                try {
//                    String outputAddress = new String(transactionOutput.getScriptPubKey().getPubKey(), "utf8");
//                    return outputAddress.equals(publicAddress);
//                } catch (Exception e) {
//                    logger.warn("Error claiming tx output", e);
//                    return false;
//                }
//            }
//        }));
//    }
//
//    /**
//     * Discard and log any transactions that invalid
//     */
//    private List<RemoteTransaction> discardInvalidTransactions(List<RemoteTransaction> incomingTransactions, final BigDecimal minimalAmount) {
//        return newArrayList(Collections2.filter(incomingTransactions, new Predicate<RemoteTransaction>() {
//            @Override
//            public boolean apply(RemoteTransaction remoteTransaction) {
//                List<TransactionOutput> outputs = remoteTransaction.getTransaction().getOutputs();
//                if (outputs.size() < 1 || outputs.size() > 2) {
//                    // http://bitcoin.stackexchange.com/questions/2366/in-order-to-pay-someone-back-must-i-ask-them-for-a-return-address
//                    logger.warn("Got weird transaction that does not have exactly one or two output " + remoteTransaction);
//                    return false;
//                }
//                if (remoteTransaction.getTransaction().getInputs().isEmpty()) {
//                    logger.warn("No input on transaction. Did someone mine this onto our address?" + remoteTransaction);
//                    return false;
//                }
//                TransactionOutput output = outputs.get(0);
//                return Stratum.satoshisToBitcoin(output.getValue()).compareTo(minimalAmount) >= 0 &&
//                        remoteTransaction.getConfirmations() > 0;
//            }
//        }));
//    }
}
