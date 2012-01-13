package org.bitcoin.stratum;

import com.google.bitcoin.core.NetworkParameters;
import org.junit.Test;
import play.test.UnitTest;

public class StratumTests extends UnitTest {

    private final String someTestnetAddress = "mw5h6BDh77GUPc4DCmzjoU7ry2R4exoaYr";
    private final String someProdAddress = "14obksEpeUTAKsdKsw3vTSUoiYeK9S3thA";

    @Test
    public void sanity() {
        Stratum stratum = new Stratum(NetworkParameters.testNet(), "http://chicago.stratum.bitcoin.cz:8000/");

        AddressInfo addressInfo = stratum.getAddressInfo(someProdAddress);

        System.out.println("Foobar");
    }
}
