package org.bitcoin.stratum;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class Stratum {
    public Stratum(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
    }

    private final NetworkParameters networkParameters;

    public static BigDecimal satoshisToBitcoin(BigInteger satoshis) {
        return new BigDecimal("0.00000001").multiply(new BigDecimal(satoshis));
    }

    public KeyPair newKeyPair() {
        ECKey key = new ECKey();
        return new KeyPair(key.toAddress(networkParameters).toString(), key.getPrivKeyBytes());
    }

    /**
     * Returns a list of all transaction that went to {@code address}, sorted by date.
     */
    public List<RemoteTransaction> getRemoteTransactions(String address) {
        // TODO
        return newArrayList();
    }

    public void sendTransaction(OutgoingRemoteTransaction outgoingTx) {
    }

    public AddressInfo getAddressInfo(String publicAddress) {
        // TODO
        return null;
    }

    public Map<String, AddressInfo> getAddressInfo(Collection<String> allPublicAddress) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * For now, this is a constant
     */
    public BigDecimal getTransactionFee() {
        return new BigDecimal("0.005");
    }
}

