package controllers;

import models.ExpectedTransaction;
import play.Play;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatest();
        boolean testnet = !Play.configuration.get("network").equals("prodNet");
        render(transaction, testnet);
//        foo();
    }

    private static void foo() {
        try {
            bar();
        } catch (Exception e) {
            throw new RuntimeException("Oh yeah?", e);
        }

    }

    private static void bar() {
        throw new RuntimeException("Up yours");
    }

    public static void faq() {
        ExpectedTransaction transaction = ExpectedTransaction.getLatest();
        render(transaction);
    }

    public static void status() {
        renderText("Alive");
    }
}