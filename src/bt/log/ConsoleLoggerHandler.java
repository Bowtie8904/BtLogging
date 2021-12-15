package bt.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class ConsoleLoggerHandler extends LoggerHandler
{
    public ConsoleLoggerHandler(LoggerConfiguration config)
    {
        super(config);
    }

    @Override
    protected void doLog(String prefix, LogRecord record)
    {
        if (record.getLevel().equals(Level.SEVERE))
        {
            System.err.println(prefix + record.getMessage());
        }
        else
        {
            System.out.println(prefix + record.getMessage());
        }
    }
}