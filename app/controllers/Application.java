package controllers;

import models.ExpectedTransaction;
import org.bitcoin.stratum.StratumHolder;
import play.Play;
import play.mvc.Controller;

public class Application extends Controller {
    public static void index() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatestValidated();
        boolean testnet = !Play.configuration.get("network").equals("prodNet");
        render(transaction, testnet);
    }

    public static void faq() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatestValidated();
        render(transaction);
    }

    public static void status() {
        renderText("Alive");
    }

    public static String getPaymentAddress(String returnAddress) {
        ExpectedTransaction lastValidTransaction = ExpectedTransaction.getLatestValidated();
        ExpectedTransaction nextTransaction = new ExpectedTransaction(StratumHolder.Stratum.newKeyPair(), returnAddress, BookKeeper.nextMinimalAmount(lastValidTransaction));
        nextTransaction.save();
        return nextTransaction.publicAddress;
    }
}

