package org.bitcoin.stratum;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class OutgoingRemoteTransaction {
    private List<byte[]> inputs = newArrayList();
    private List<OutgoingRemotePayment> outputs = newArrayList();

    public OutgoingRemoteTransaction addPayment(OutgoingRemotePayment payment) {
        outputs.add(payment);
        return this;
    }

    public OutgoingRemoteTransaction addInputKey(byte[] privateKey) {
        inputs.add(privateKey);
        return this;
    }
}
