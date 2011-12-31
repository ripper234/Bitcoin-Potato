package org.bitcoin.stratum;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import org.apache.log4j.Logger;
import util.LogUtil;

import java.math.BigInteger;

/**
 * Copy pasted from com.google.bitcoin.core.Wallet
 * <p/>
 * https://groups.google.com/forum/#!topic/bitcoinj/Wm9P9j5n7yM
 */
public class BitcoinjWallet {
    private static final Logger log = LogUtil.getLogger();

    public static Transaction createSend(Address address, BigInteger nanocoins, Address changeAddress, NetworkParameters params) {
        throw new RuntimeException();
        // TODO
//        log.info("Creating send tx to " + address.toString() + " for " +
//                bitcoinValueToFriendlyString(nanocoins));
//        // To send money to somebody else, we need to do gather up transactions with unspent outputs until we have
//        // sufficient value. Many coin selection algorithms are possible, we use a simple but suboptimal one.
//        // TODO: Sort coins so we use the smallest first, to combat wallet fragmentation and reduce fees.
//        BigInteger valueGathered = BigInteger.ZERO;
//        List<TransactionOutput> gathered = new LinkedList<TransactionOutput>();
//        for (Transaction tx : unspent.values()) {
//            for (TransactionOutput output : tx.getOutputs()) {
//                if (!output.isAvailableForSpending()) continue;
//                if (!output.isMine(this)) continue;
//                gathered.add(output);
//                valueGathered = valueGathered.add(output.getValue());
//            }
//            if (valueGathered.compareTo(nanocoins) >= 0) break;
//        }
//
//        // Can we afford this?
//        if (valueGathered.compareTo(nanocoins) < 0) {
//            log.info("Insufficient value in wallet for send, missing " +
//                    bitcoinValueToFriendlyString(nanocoins.subtract(valueGathered)));
//            // TODO: Should throw an exception here.
//            return null;
//        }
//
//        Preconditions.checkArgument(gathered.size() > 0);
//
//        Transaction sendTx = new Transaction(params);
//        sendTx.addOutput(new TransactionOutput(params, sendTx, nanocoins, address));
//        BigInteger change = valueGathered.subtract(nanocoins);
//        if (change.compareTo(BigInteger.ZERO) > 0) {
//            // The value of the inputs is greater than what we want to send. Just like in real life then,
//            // we need to take back some coins ... this is called "change". Add another output that sends the change
//            // back to us.
//            log.info("  with " + bitcoinValueToFriendlyString(change) + " coins change");
//            sendTx.addOutput(new TransactionOutput(params, sendTx, change, changeAddress));
//        }
//        for (TransactionOutput output : gathered) {
//            sendTx.addInput(output);
//        }
//
//        // Now sign the inputs, thus proving that we are entitled to redeem the connected outputs.
//        try {
//            sendTx.signInputs(Transaction.SigHash.ALL, this);
//        } catch (ScriptException e) {
//            // If this happens it means an output script in a wallet tx could not be understood. That should never
//            // happen, if it does it means the wallet has got into an inconsistent state.
//            throw new RuntimeException(e);
//        }
//        log.info("  created " + sendTx.getHashAsString());
//        return sendTx;
    }
}
