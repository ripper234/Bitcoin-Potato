package models;

import org.apache.log4j.Level;
import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Log4jLine extends Model {
    public Log4jLine() {
    }

    public Log4jLine(Date eventTime, Level level, String component, String message, String stackTrace, String processName, int threadId, String threadName, String machineName) {
        this.eventTime = eventTime;
        this.level = level;
        this.component = component;
        this.message = message;
        this.stackTrace = stackTrace;
        this.processName = processName;
        this.threadId = threadId;
        this.threadName = threadName;
        this.machineName = machineName;
    }

    public Date eventTime;
    public Level level;
    public String component;
    public String message;
    public String stackTrace;
    public String processName;
    public int threadId;
    public String threadName;
    public String machineName;
}
