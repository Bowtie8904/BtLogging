package bt.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class FileLoggerHandler extends FileHandler
{
    public static final String DEFAULT_FILE_PATTERN = "./logs/default_logfile%u.log";

    public FileLoggerHandler() throws IOException, SecurityException
    {
        super(DEFAULT_FILE_PATTERN);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(String pattern) throws IOException, SecurityException
    {
        super(pattern);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(String pattern, boolean append) throws IOException, SecurityException
    {
        super(pattern, append);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(String pattern, int limit, int count) throws IOException, SecurityException
    {
        super(pattern, limit, count);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException
    {
        super(pattern, limit, count, append);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(String pattern, long limit, int count, boolean append) throws IOException
    {
        super(pattern, limit, count, append);
        setFormatter(new DefaultLogFormatter(new LoggerConfiguration()));
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(LoggerConfiguration config) throws IOException, SecurityException
    {
        super(DEFAULT_FILE_PATTERN);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(LoggerConfiguration config, String pattern) throws IOException, SecurityException
    {
        super(pattern);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(LoggerConfiguration config, String pattern, boolean append) throws IOException, SecurityException
    {
        super(pattern, append);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(LoggerConfiguration config, String pattern, int limit, int count) throws IOException, SecurityException
    {
        super(pattern, limit, count);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(LoggerConfiguration config, String pattern, int limit, int count, boolean append) throws IOException, SecurityException
    {
        super(pattern, limit, count, append);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(LoggerConfiguration config, String pattern, long limit, int count, boolean append) throws IOException
    {
        super(pattern, limit, count, append);
        setFormatter(new DefaultLogFormatter(config));
        setLevel(config.level);
    }

    public FileLoggerHandler(Formatter formatter) throws IOException, SecurityException
    {
        super(DEFAULT_FILE_PATTERN);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(Formatter formatter, String pattern) throws IOException, SecurityException
    {
        super(pattern);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(Formatter formatter, String pattern, boolean append) throws IOException, SecurityException
    {
        super(pattern, append);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(Formatter formatter, String pattern, int limit, int count) throws IOException, SecurityException
    {
        super(pattern, limit, count);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(Formatter formatter, String pattern, int limit, int count, boolean append) throws IOException, SecurityException
    {
        super(pattern, limit, count, append);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }

    public FileLoggerHandler(Formatter formatter, String pattern, long limit, int count, boolean append) throws IOException
    {
        super(pattern, limit, count, append);
        setFormatter(formatter);
        setLevel(Level.ALL);
    }
}