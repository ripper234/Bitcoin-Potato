package com.bitcoinpotato.overlay;

public class KeyPair {
    public KeyPair(String publicAddress, byte[] privateKey) {

        this.publicAddress = publicAddress;
        this.privateKey = privateKey;
    }

    public final String publicAddress;
    public final byte[] privateKey;
}
