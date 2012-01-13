package jobs;

import com.bitcoinpotato.util.LogUtil;
import models.IncomingTransaction;
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

        if (IncomingTransaction.count() > 0) {
            logger.info(String.format("Found %d existing transaction", IncomingTransaction.count()));
            return;
        }

        // Initial bootstrap
        BigDecimal startingFee = new BigDecimal("0.01");
        IncomingTransaction firstTransaction = new IncomingTransaction(
                StratumHolder.Stratum.newKeyPair(),
                (String) Play.configuration.get("housePublicAddress"), // first payment is to the house
                startingFee);

        firstTransaction.status = IncomingTransaction.Status.Valid;
        firstTransaction.actualAmount = startingFee;
        firstTransaction.save();
        logger.info("Successfully initialized");
    }
}
