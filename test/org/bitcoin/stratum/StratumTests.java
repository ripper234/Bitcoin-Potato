package org.bitcoin.stratum;

import com.google.bitcoin.core.NetworkParameters;
import org.junit.Test;
import play.test.UnitTest;

public class StratumTests extends UnitTest {
    @Test
    public void sanity() {
        Stratum stratum = new Stratum(NetworkParameters.testNet(), "http://chicago.stratum.bitcoin.cz:8000/");
        String someTestnetAddress = "mw5h6BDh77GUPc4DCmzjoU7ry2R4exoaYr";

        AddressInfo addressInfo = stratum.getAddressInfo(someTestnetAddress);

        System.out.println("Foobar");
    }
}
