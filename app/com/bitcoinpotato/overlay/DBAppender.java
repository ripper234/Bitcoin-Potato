package com.bitcoinpotato.overlay;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.net.UnknownHostException;

public class DBAppender extends AppenderSkeleton {
    @Override
    protected void append(LoggingEvent loggingEvent) {
//        String join = Joiner.on("\r\n").join(Lists.asList(loggingEvent.getThrowableStrRep()));
//        Log4jLine logLine = new Log4jLine(
//                new Date(loggingEvent.getTimeStamp()),
//                loggingEvent.getLevel(),
//                loggingEvent.getLoggerName(),
//                loggingEvent.getMessage(),
//                join,
//                ManagementFactory.getRuntimeMXBean().getName(),
//                Thread.currentThread().getId(),
//                Thread.currentThread().getName(),
//                getHostname()
//                );

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
