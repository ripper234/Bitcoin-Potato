package org.play.logging;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Date;

public class DBAppender extends AppenderSkeleton {
    private final String processName = ManagementFactory.getRuntimeMXBean().getName();
    private final String hostname = getHostname();

    @Override
    protected void append(LoggingEvent loggingEvent) {
        Log4jLine logLine = new Log4jLine(
                new Date(loggingEvent.getTimeStamp()),
                loggingEvent.getLevel(),
                loggingEvent.getLoggerName(),
                loggingEvent.getRenderedMessage(),
                getStackTraceStr(loggingEvent),
                processName,
                Thread.currentThread().getId(),
                Thread.currentThread().getName(),
                hostname
        );

        // http://stackoverflow.com/a/8689444/11236
        // Save the log line in a separate transaction
        EntityManager em = JPA.newEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(loggingEvent);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
    }

    private String getStackTraceStr(LoggingEvent loggingEvent) {
        return loggingEvent.getThrowableStrRep() == null ? null :
                Joiner.on("\r\n").join(Lists.newArrayList(loggingEvent.getThrowableStrRep()));
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
