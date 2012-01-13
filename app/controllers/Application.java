package controllers;

import models.IncomingTransaction;
import org.bitcoin.stratum.StratumHolder;
import play.Play;
import play.mvc.Controller;

public class Application extends Controller {
    public static void index() {
        IncomingTransaction latestTx = IncomingTransaction.getLatestValid();
        boolean testnet = !Play.configuration.get("network").equals("prodNet");
        render(latestTx, testnet);
    }

    public static void faq() {
        IncomingTransaction transaction = IncomingTransaction.getLatestValid();
        render(transaction);
    }

    public static void status() {
        renderText("Alive");
    }

    public static String getPaymentAddress(String returnAddress) {
        IncomingTransaction lastValidTransaction = IncomingTransaction.getLatestValid();
        IncomingTransaction nextTransaction = new IncomingTransaction(StratumHolder.Stratum.newKeyPair(), returnAddress, BookKeeper.nextMinimalAmount(lastValidTransaction));
        nextTransaction.save();
        return nextTransaction.publicAddress;
    }

}

