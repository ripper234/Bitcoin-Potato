package jobs;

import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import models.ExpectedTransaction;
import org.apache.log4j.Logger;
import org.bitcoin.stratum.*;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import util.Collections3;
import util.LogUtil;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
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
        logger.info("Transaction poller running");

        ExpectedTransaction expectedTransaction = ExpectedTransaction.getLatest();
        List<RemoteTransaction> incomingTransactions = StratumHolder.Stratum.getIncomingTransactions(expectedTransaction.publicAddress);

        incomingTransactions = discardInvalidTransactions(incomingTransactions, expectedTransaction.minimalAmount);
        if (incomingTransactions.isEmpty()) {
            // nothing to do for now
            logger.debug("No incoming transactions found");
            return;
        }

        // Check if there is more than one valid transaction.
        // The odds of this happening is slim, since we're polling every ten seconds.
        // Let's log it and pick the first
        if (incomingTransactions.size() > 1) {
            logger.warn(String.format("More than one transaction detected into %s: %s",
                    expectedTransaction.publicAddress,
                    Joiner.on(",").join(incomingTransactions)));

            // let's not make any progress in this state (better safe than sorry)
            return;
        }

        // Set up the next expected transaction
        RemoteTransaction actualTransaction = Collections3.single(incomingTransactions);

        logger.info("Got transaction: " + actualTransaction);

        // Sanity check - the lower transactions are actually filtered beforehand.
        TransactionOutput output = findOurOutputs(actualTransaction.getTransaction().getOutputs(), expectedTransaction.publicAddress);
        BigDecimal btcOutput = Stratum.satoshisToBitcoin(output.getValue());
        checkArgument(btcOutput.compareTo(expectedTransaction.minimalAmount) >= 0);

        BigDecimal nextMinimalAmount = btcOutput.multiply(velocity);

        List<TransactionInput> inputs = actualTransaction.getTransaction().getInputs();
        checkArgument(inputs.size() > 0);
        ExpectedTransaction nextExpectedTx = new ExpectedTransaction(StratumHolder.Stratum.newKeyPair(), inputs.get(0).getFromAddress().toString(), nextMinimalAmount);

        logger.info("Next payment will be at least " + nextExpectedTx.minimalAmount + " BTC");

        BigDecimal commission = commissionRate.multiply(btcOutput);
        BigDecimal payout = btcOutput.subtract(commission);

        // TODO - Ideally, this next part should all be in one distributed transaction
        // Think about some clever thing to do in case of partial failure here.
        OutgoingRemoteTransaction outgoingTx = new OutgoingRemoteTransaction()
                .addPayment(new OutgoingRemotePayment(expectedTransaction.payoutAddress, payout))
                .addPayment(new OutgoingRemotePayment(housePublicAddress, payout))
                .addInputKey(expectedTransaction.privateKey);

        StratumHolder.Stratum.sendTransaction(outgoingTx);
        nextExpectedTx.save();
    }

    /**
     * Returns those outputs that match our address
     */
    private TransactionOutput findOurOutputs(List<TransactionOutput> outputs, final String publicAddress) {
        return Collections3.single(Collections2.filter(outputs, new Predicate<TransactionOutput>() {
            @Override
            public boolean apply(TransactionOutput transactionOutput) {
                try {
                    String outputAddress = new String(transactionOutput.getScriptPubKey().getPubKey(), "utf8");
                    return outputAddress.equals(publicAddress);
                } catch (Exception e) {
                    logger.warn("Error claiming tx output", e);
                    return false;
                }
            }
        }));
    }

    /**
     * Discard and log any transactions that invalid
     */
    private List<RemoteTransaction> discardInvalidTransactions(List<RemoteTransaction> incomingTransactions, final BigDecimal minimalAmount) {
        return newArrayList(Collections2.filter(incomingTransactions, new Predicate<RemoteTransaction>() {
            @Override
            public boolean apply(RemoteTransaction remoteTransaction) {
                List<TransactionOutput> outputs = remoteTransaction.getTransaction().getOutputs();
                if (outputs.size() < 1 || outputs.size() > 2) {
                    // http://bitcoin.stackexchange.com/questions/2366/in-order-to-pay-someone-back-must-i-ask-them-for-a-return-address
                    logger.warn("Got weird transaction that does not have exactly one or two output " + remoteTransaction);
                    return false;
                }
                if (remoteTransaction.getTransaction().getInputs().isEmpty()) {
                    logger.warn("No input on transaction. Did someone mine this onto our address?" + remoteTransaction);
                    return false;
                }
                TransactionOutput output = outputs.get(0);
                return Stratum.satoshisToBitcoin(output.getValue()).compareTo(minimalAmount) >= 0 &&
                        remoteTransaction.getConfirmations() > 0;
            }
        }));
    }
}
