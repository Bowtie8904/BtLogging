package bt.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class Logging
{
    private static final String GLOBAL_LOGGER_NAME = "GLOBAL";
    private static final Logger GLOBAL_LOGGER = LoggerFactory.getLogger(GLOBAL_LOGGER_NAME);

    public static Logger global()
    {
        return GLOBAL_LOGGER;
    }

    public static void configureDefaultGlobalLogger()
    {
        configureDefaultGlobalLogger(new LoggerConfiguration().level(Level.ALL));
    }

    public static void configureDefaultGlobalLogger(LoggerConfiguration config)
    {
        configureDefaultGlobalLogger(new ConsoleLoggerHandler(config));
    }

    public static void configureDefaultGlobalLogger(LoggerHandler firstHandler, LoggerHandler... handlers)
    {
        // remove the default handler from the root logger
        LogManager.getLogManager().reset();

        java.util.logging.Logger globalJdkLogger = java.util.logging.Logger.getLogger(GLOBAL_LOGGER_NAME);

        globalJdkLogger.setLevel(Level.ALL);
        globalJdkLogger.addHandler(firstHandler);

        for (var handler : handlers)
        {
            globalJdkLogger.addHandler(handler);
        }
    }
}