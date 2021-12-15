package bt.log;

import java.util.logging.Level;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class LoggerConfiguration
{
    protected Level level;
    protected String[] invalidCallerPackages;
    protected Class<?>[] invalidCallerClasses;
    protected boolean printTimestamp = true;
    protected boolean printCaller = true;
    protected boolean printThreadName = true;
    protected boolean printLogLevel= true;

    public LoggerConfiguration()
    {

    }

    public LoggerConfiguration level(Level logLevel)
    {
        this.level = logLevel;
        return this;
    }

    public LoggerConfiguration invalidCallerPackages(String... packages)
    {
        this.invalidCallerPackages = packages;
        return this;
    }

    public LoggerConfiguration invalidCallerClasses(Class<?>... classes)
    {
        this.invalidCallerClasses = classes;
        return this;
    }

    public LoggerConfiguration printTimestamp(boolean value)
    {
        this.printTimestamp = value;
        return this;
    }

    public LoggerConfiguration printCaller(boolean value)
    {
        this.printCaller = value;
        return this;
    }

    public LoggerConfiguration printThreadName(boolean value)
    {
        this.printThreadName = value;
        return this;
    }

    public LoggerConfiguration printLogLevel(boolean value)
    {
        this.printLogLevel = value;
        return this;
    }
}