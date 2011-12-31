package util;

import org.apache.log4j.Logger;

public class LogUtil {
    private LogUtil(){}

    public static Logger getLogger() {
        // http://stackoverflow.com/a/1814688/11236
        String callingClassName = Thread.currentThread().getStackTrace()[2].getClass().getCanonicalName();
        return Logger.getLogger(callingClassName);
    }
}
