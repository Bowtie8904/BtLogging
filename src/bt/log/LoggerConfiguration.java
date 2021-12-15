package bt.log;

import java.util.logging.Level;

/**
 * A class holding values to set up a {@link LoggerHandler}.
 *
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class LoggerConfiguration
{
    /**
     * The minimum log level.
     */
    protected Level level;

    /**
     * A list of packages which should be ignored when searching for the calling method.
     */
    protected String[] invalidCallerPackages;

    /**
     * A list of classes which should be ignored when searching for the calling method.
     */
    protected Class<?>[] invalidCallerClasses;

    /**
     * Indicates whether log entries should contain a timestamp.
     */
    protected boolean printTimestamp = true;

    /**
     * Indicates whether log entries should contain the caller information.
     */
    protected boolean printCaller = true;

    /**
     * Indicates whether log entries should contain the thread name.
     */
    protected boolean printThreadName = true;

    /**
     * Indicates whether log entries should contain the log level.
     */
    protected boolean printLogLevel = true;

    /**
     * Creates a new instance.
     */
    public LoggerConfiguration()
    {
        this.level = Level.ALL;
        this.invalidCallerClasses = new Class<?>[0];
        this.invalidCallerPackages = new String[0];
    }

    /**
     * Sets the loglevel of this configuration.
     *
     * @param logLevel
     *
     * @return
     */
    public LoggerConfiguration level(Level logLevel)
    {
        this.level = logLevel;
        return this;
    }

    /**
     * Sets the invalid caller packages of this configuration.
     *
     * @param packages
     *
     * @return
     */
    public LoggerConfiguration invalidCallerPackages(String... packages)
    {
        this.invalidCallerPackages = packages;
        return this;
    }

    /**
     * Sets the invalid caller classes of this configuration.
     *
     * @param classes
     *
     * @return
     */
    public LoggerConfiguration invalidCallerClasses(Class<?>... classes)
    {
        this.invalidCallerClasses = classes;
        return this;
    }

    /**
     * Sets the flag for the timestamp in the log entries of this configuration.
     *
     * @param value
     *
     * @return
     */
    public LoggerConfiguration printTimestamp(boolean value)
    {
        this.printTimestamp = value;
        return this;
    }

    /**
     * Sets the flag for the caller information in the log entries of this configuration.
     *
     * @param value
     *
     * @return
     */
    public LoggerConfiguration printCaller(boolean value)
    {
        this.printCaller = value;
        return this;
    }

    /**
     * Sets the flag for the thread name in the log entries of this configuration.
     *
     * @param value
     *
     * @return
     */
    public LoggerConfiguration printThreadName(boolean value)
    {
        this.printThreadName = value;
        return this;
    }

    /**
     * Sets the flag for the log level in the log entries of this configuration.
     *
     * @param value
     *
     * @return
     */
    public LoggerConfiguration printLogLevel(boolean value)
    {
        this.printLogLevel = value;
        return this;
    }
}