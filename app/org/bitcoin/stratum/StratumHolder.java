package org.bitcoin.stratum;

import com.google.bitcoin.core.NetworkParameters;
import play.Play;

import static com.google.common.base.Preconditions.checkNotNull;

public class StratumHolder {
    private StratumHolder() {
    }

    public static final Stratum Stratum = new Stratum(readNetworkParameters(), "http://chicago.stratum.bitcoin.cz:8000/");

    private static NetworkParameters readNetworkParameters() {
        String network = getNetworkType();
        checkNotNull(network);

        NetworkParameters networkParameters;

        if (network.equals("testNet"))
            networkParameters = NetworkParameters.testNet();
        else if (network.equals("prodNet"))
            networkParameters = NetworkParameters.prodNet();
        else throw new RuntimeException("Unknown network: " + network);
        return networkParameters;
    }

    private static String getNetworkType() {
        return (String) Play.configuration.get("network");
    }
}
