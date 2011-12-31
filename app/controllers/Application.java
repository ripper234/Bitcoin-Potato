package controllers;

import models.ExpectedTransaction;
import play.Play;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatest();
        boolean testnet = !Play.configuration.get("network").equals("prodNet");
        render(transaction, testnet);
    }

    public static void faq() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatest();
        render(transaction);
    }
}