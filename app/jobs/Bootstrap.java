package jobs;

import com.bitcoinpotato.util.LogUtil;
import models.ExpectedTransaction;
import org.apache.log4j.Logger;
import org.bitcoin.stratum.StratumHolder;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.math.BigDecimal;

@OnApplicationStart
public class Bootstrap extends Job {
    private static final Logger logger = LogUtil.getLogger();

    @Override
    public void doJob() throws Exception {
        // TODO - (This fails in Prod mode)
        // Logger.getRootLogger().addAppender(new DBAppender());

        logger.info("Bootstrapping");

        if (ExpectedTransaction.count() > 0) {
            logger.info(String.format("Found %d existing transaction", ExpectedTransaction.count()));
            return;
        }

        // Initial bootstrap
        ExpectedTransaction nextPayment = new ExpectedTransaction(
                StratumHolder.Stratum.newKeyPair(),
                (String) Play.configuration.get("houseAddress"), // first payment is to the house
                new BigDecimal("0.01")
        );
        nextPayment.save();
        logger.info("Successfully initialized");
    }
}
