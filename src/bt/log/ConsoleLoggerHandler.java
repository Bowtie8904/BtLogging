package bt.log;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A simple handler that prints given log entries to either System.out or System.err.
 * <p>
 * Log entries with a level of SEVERE or WARNING will be printed to System.err, others to System.out.
 *
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class ConsoleLoggerHandler extends Handler
{
    /**
     * Creates a new instance with the given configuration.
     * A {@link DefaultLogFormatter} will be created with the given configuration.
     *
     * @param config
     */
    public ConsoleLoggerHandler(LoggerConfiguration config)
    {
        super();
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.getLevel());
    }

    /**
     * Creates a new instance with the given formatter.
     *
     * @param formatter
     */
    public ConsoleLoggerHandler(Formatter formatter)
    {
        super();
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    /**
     * Creates a new instance with the given configuration.
     * A {@link DefaultLogFormatter} will be created with a defult configuration.
     *
     * @param config
     */
    public ConsoleLoggerHandler()
    {
        super();
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    @Override
    public void publish(LogRecord record)
    {
        if (record.getLevel().intValue() >= getLevel().intValue())
        {
            String text = getFormatter().format(record);

            if (record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING))
            {
                System.err.print(text);
            }
            else
            {
                System.out.print(text);
            }
        }
    }

    @Override
    public void flush()
    {

    }

    @Override
    public void close() throws SecurityException
    {

    }
}