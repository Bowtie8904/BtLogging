package bt.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Lukas Hartwig
 * @since 22.01.2022
 */
public class BtLoggerFactory implements ILoggerFactory
{
    ConcurrentMap<String, Logger> loggerMap;

    public BtLoggerFactory()
    {
        loggerMap = new ConcurrentHashMap<String, Logger>();
        // ensure jul initialization. see SLF4J-359
        // note that call to java.util.logging.LogManager.getLogManager() fails on the Google App Engine platform. See SLF4J-363
        java.util.logging.Logger.getLogger("");
    }

    public Logger getLogger(String name)
    {
        name = "GLOBAL";

        Logger slf4jLogger = this.loggerMap.get(name);

        if (slf4jLogger != null)
        {
            return slf4jLogger;
        }
        else
        {
            java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger(name);
            Logger newInstance = new JDK14LoggerAdapter(julLogger);
            Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }
}