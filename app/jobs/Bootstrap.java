package jobs;

import com.bitcoinpotato.overlay.StratumHolder;
import models.ExpectedTransaction;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.math.BigDecimal;

@OnApplicationStart
public class Bootstrap extends Job {
    @Override
    public void doJob() throws Exception {
        if (ExpectedTransaction.count() > 0) {
            return;
        }

        // Initial bootstrap
        ExpectedTransaction nextPayment = new ExpectedTransaction(
                StratumHolder.Stratum.newKeyPair(),
                (String) Play.configuration.get("houseAddress"), // first payment is to the house
                new BigDecimal("0.01")
        );
        nextPayment.save();
    }
}
