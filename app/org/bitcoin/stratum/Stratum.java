package org.bitcoin.stratum;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Function;
import com.google.gson.JsonObject;
import org.bitcoinpotato.util.Maps3;
import play.libs.WS;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class Stratum {
    public Stratum(NetworkParameters networkParameters, String startumServerUrl) {
        this.networkParameters = networkParameters;
        this.startumServerUrl = startumServerUrl;
    }

    private final NetworkParameters networkParameters;
    private final String startumServerUrl;

    public static BigDecimal satoshisToBitcoin(BigInteger satoshis) {
        return new BigDecimal("0.00000001").multiply(new BigDecimal(satoshis));
    }

    public KeyPair newKeyPair() {
        ECKey key = new ECKey();
        return new KeyPair(key.toAddress(networkParameters).toString(), key.getPrivKeyBytes());
    }

    public void sendTransaction(OutgoingRemoteTransaction outgoingTx) {

    }

    public AddressInfo getAddressInfo(String publicAddress) {
        WS.HttpResponse response = null;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("key1", "value1");
            jsonObject.addProperty("key2", "value2");
            jsonObject.add("key3", new JsonObject());
            response = WS.url(startumServerUrl)
                    .setHeader("content-type", "application/stratum")
                            //.body(json)
                    .setParameter("id", 1)
                    .setParameter("method", "blockchain.address.get_history")
                    .setParameter("params", publicAddress)
                    .timeout("60s")
                    .post();
        } catch (Exception e) {
            throw new RuntimeException("Failure doing Startum.getAddress with address " + publicAddress);
        }

        checkArgument(response.getStatus() == 200);

        // TODO
        return new AddressInfo(new BigDecimal("0.01"));
    }

    /**
     * A bulk API version
     */
    public Map<String, AddressInfo> getAddressInfo(Collection<String> allPublicAddress) {
        // Naive implementation for now

        return Maps3.build(allPublicAddress, new Function<String, AddressInfo>() {
            public AddressInfo apply(@Nullable String s) {
                return null;
            }
        });
    }

    /**
     * For now, this is a constant
     */
    public BigDecimal getTransactionFee() {
        return new BigDecimal("0.0005");
    }
}

